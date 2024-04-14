const TransactionUser = require('../models/transactionUser.model');
const Refund = require('../models/refund.model');
const Transaction = require('../models/transaction.model');

const refundTransactions = async (req, res) => {
    console.log(`REST refundTransactions`);
    const {transactions} = req.body;
    let refundsList = [];
    if (Array.isArray(transactions)) {
        try {
            for (const transaction of transactions) {
                const transactionUser = await TransactionUser.findOne({
                    where: {
                        id: transaction
                    },
                    include: {
                        model: Transaction
                    }
                });
                if (!transactionUser) {
                    return res.status(404).send('Transaction not found');
                }
                if (transactionUser.userId !== req.params.userId) {
                    return res.status(403).send('You are not allowed to refund this transaction');
                }
                console.log(transactionUser.refunded);
                if (transactionUser.refunded) {
                    return res.status(400).send('Transaction already refunded');
                }
                const refund = await Refund.create({
                    transactionId: transactionUser.id,
                    refundingUserId: req.params.userId,
                    refundedUserId: transactionUser.Transaction.senderId,
                    amount: transactionUser.amount
                });
                refundsList.push(refund);
                transactionUser.refunded = true;
                await transactionUser.save();
            }
            return res.status(201).send(refundsList);
        } catch (error) {
            console.error(error);
            return res.status(500).send('Error refunding transactions');
        }
    } else {
        return res.status(400).send('No transaction to refund or transactions is not an array');
    }
}

const getUserDebtForOtherGroupsUsers = async (req, res) => {
    console.log(`REST getUserDebtForOtherGroupsUsers`);
    const {userId, groupId} = req.params;
    try {
        let totalArray = {};
        let debts = await TransactionUser.findAll({
            where: {
                userId
            },
            include: {
                model: Transaction
            }
        });
        for (const debt of debts) {
            if (debt.Transaction.senderId !== userId) {
                console.log(debt.Transaction);
                if (!totalArray[debt.Transaction.senderId]) {
                    totalArray[debt.Transaction.senderId] = 0;
                }
                totalArray[debt.Transaction.senderId] += debt.amount;
            }
        }
        return res.status(200).send(totalArray);
    } catch (error) {
        console.error(error);
        return res.status(500).send('Error getting user debts');
    }
}

module.exports = {
    refundTransactions,
    getUserDebtForOtherGroupsUsers
}