const Transaction = require('../models/transaction.model');
const TransactionUser = require('../models/transactionUser.model');
const UserGroup = require('../models/userGroup.model');
const Group = require('../models/group.model');
const {where} = require("sequelize");

const createTransaction = async (req, res) => {
    console.log(`REST createTransaction`);
    const {groupId, label, total_amount, date, receipt, senderId, categoryId, details} = req.body;
    if (total_amount <= 0) {
        return res.status(400).send('The total amount of the transaction must be greater than 0');
    }
    try {
        if (parseInt(senderId) !== parseInt(req.authorization.userId)) {
            return res.status(403).send('You are not the sender of the transaction');
        }
        let transaction = await Transaction.create({
            groupId,
            label,
            total_amount,
            date,
            receipt,
            senderId,
            categoryId
        });

        const detailsArray = Object.values(details);
        let totalDetailAmount = 0;

        for (const detail of detailsArray) {
            totalDetailAmount += detail.amount;

            const userInGroup = await UserGroup.findOne({
                where: {
                    groupId: groupId,
                    userId: detail.userId
                }
            });

            if (!userInGroup) {
                return res.status(400).send('User '+ detail.userId + ' is not part of the group');
            }
        }

        if (totalDetailAmount === total_amount) {
            for (const detail of detailsArray) {
                if (detail.amount > 0) {
                    await TransactionUser.create({
                        transactionId: transaction.id,
                        userId: detail.userId,
                        amount: detail.amount
                    });
                    let userGroup = await UserGroup.findOne({
                        where: {
                            groupId: groupId,
                            userId: detail.userId
                        }
                    });
                    let newBalance = userGroup.balance;
                    if (parseInt(detail.userId) !== parseInt(senderId)) {
                        newBalance = userGroup.balance - detail.amount;
                    } else {
                        newBalance = userGroup.balance + total_amount - detail.amount;
                    }
                    await UserGroup.update(
                        { balance: newBalance },
                        {
                            where: {
                                groupId: groupId,
                                userId: detail.userId
                            }
                        }
                    );
                }
            }
            return res.status(201).send(transaction);
        } else {
            return res.status(400).send('The total amount of the transaction is not equal to the sum of the details');
        }
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}

const getTransaction = async (req, res) => {
    console.log(`REST getTransaction`);
    try {
        const transaction = await Transaction.findByPk(req.params.id, {
            include: [{
                model: TransactionUser,
                attributes: ['userId', 'amount']
            }]
        });        if(transaction === null) {
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
        const transaction = await Transaction.findAll({
            where: {
                senderId: req.params.userId
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

const getXLastsUserTransactions = async (req, res) => {
    console.log(`REST getTransaction`);
    try {
        const limit = parseInt(req.query.limit);
        const transactions = await TransactionUser.findAll({
            where: {
                userId: req.params.userId
            },
            order: [
                ['createdAt', 'DESC']
            ],
            limit: limit,
            include: [{
                model: Transaction,
                include: [{
                    model: Group
                }]
            }]
        });
        if(transactions === null) {
            return res.status(404).send('No transaction found for this user');
        }
        return res.status(200).send(transactions);
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}


module.exports = {
    createTransaction,
    getTransaction,
    getUserTransactions,
    getXLastsUserTransactions
}