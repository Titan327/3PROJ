const Transaction = require('../models/transaction.model');
const TransactionUser = require('../models/transactionUser.model');

const createTransaction = async (req, res) => {
    console.log(`REST createTransaction`);
    const {group_id, label, total_amount, date, receipt, sender_id, category_id, details} = req.body;
    console.log(group_id, label, total_amount, date, receipt, sender_id, category_id);
    try {
        let transaction = await Transaction.create({
            group_id,
            label,
            total_amount,
            date,
            receipt,
            sender_id,
            category_id
        });

        const detailsArray = Object.values(details);
        let transactionUserIds = [];
        let totalDetailAmount = 0;

        for (const detail of detailsArray) {
            const transactionUser = await TransactionUser.create({
                transaction_id: transaction.id,
                user_id: detail.user_id,
                amount: detail.amount
            });
            transactionUserIds.push(transactionUser.id);
            totalDetailAmount += detail.amount;
        }
        if (totalDetailAmount !== total_amount) {
            await Transaction.destroy({
                where: {
                    id: transaction.id
                }
            });
            await TransactionUser.destroy({
                where: {
                    id: transactionUserIds
                }
            });
            return res.status(400).send('The sum of the details amount must be equal to the total amount');
        }
        return res.status(201).send(transaction);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const getTransaction = async (req, res) => {
    console.log(`REST getTransaction`);
    try {
        const transaction = await Transaction.findByPk(req.params.id);
        if(transaction === null) {
            return res.status(404).send('Transaction not found');
        }
        return res.status(200).send(transaction);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const getUserTransactions = async (req, res) => {
    console.log(`REST getTransaction`);
    try {
        console.log(req.params.userId);
        const transaction = await Transaction.findAll({
            where: {
                sender_id: req.params.userId
            }
        });
        if(transaction === null) {
            return res.status(404).send('No transaction found for this user');
        }
        return res.status(200).send(transaction);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}


module.exports = {
    createTransaction,
    getTransaction,
    getUserTransactions
}