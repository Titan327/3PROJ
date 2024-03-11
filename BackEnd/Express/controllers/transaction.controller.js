const Transaction = require('../models/transaction.model');
const TransactionUser = require('../models/transactionUser.model');
const UserGroup = require('../models/userGroup.model');
const Group = require('../models/group.model');

const deleteTransactionAndTransactionUsers = async (transactionId, transactionUserIds) => {
    await Transaction.destroy({
        where: {
            id: transactionId
        }
    });
    await TransactionUser.destroy({
        where: {
            id: transactionUserIds
        }
    });
}

const createTransaction = async (req, res) => {
    console.log(`REST createTransaction`);
    const {groupId, label, total_amount, date, receipt, senderId, categoryId, details} = req.body;
    console.log(groupId, label, total_amount, date, receipt, senderId, categoryId);
    try {
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
        let transactionUserIds = [];
        let totalDetailAmount = 0;

        for (const detail of detailsArray) {
            const userInGroup = await UserGroup.findOne({
                where: {
                    groupId: groupId,
                    userId: detail.userId
                }
            });

            if (!userInGroup) {
                await deleteTransactionAndTransactionUsers(transaction.id, transactionUserIds);
                return res.status(400).send('User is not part of the group');
            }

            const transactionUser = await TransactionUser.create({
                transactionId: transaction.id,
                userId: detail.userId,
                amount: detail.amount
            });
            transactionUserIds.push(transactionUser.id);
            totalDetailAmount += detail.amount;
        }
        if (totalDetailAmount !== total_amount) {
            await deleteTransactionAndTransactionUsers(transaction.id, transactionUserIds);
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