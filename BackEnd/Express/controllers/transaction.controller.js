const Transaction = require('../models/transaction.model');
const TransactionUser = require('../models/transactionUser.model');
const UserGroup = require('../models/userGroup.model');
const Group = require('../models/group.model');
const RefundController = require('./refund.controller');
const {CreateNotif} = require("./notif.controller");

const createTransaction = async (req, res) => {
    console.log(`REST createTransaction`);
    let groupId = req.params.groupId;
    let senderId = req.authorization.userId;
    const {label, total_amount, date, receipt, categoryId, details} = req.body;
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

            const amountParts = detail.amount.toString().split(".");
            if (amountParts.length > 1 && amountParts[1].length > 2) {
                return res.status(400).send('The amount of the transaction must have at most 2 decimal places');
            }

            if (detail.amount < 0) {
                return res.status(400).send('The amount of the transaction must be greater than 0');
            }

            if (!userInGroup) {
                return res.status(400).send('User '+ detail.userId + ' is not part of the group');
            }
        }

        if (parseFloat(totalDetailAmount).toFixed(2) === parseFloat(total_amount).toFixed(2)) {
            for (const detail of detailsArray) {
                if (detail.amount > 0 || parseInt(detail.userId) === parseInt(senderId)) {
                    await TransactionUser.create({
                        transactionId: transaction.id,
                        userId: detail.userId,
                        amount: parseInt(detail.amount).toFixed(2)
                    });
                    let userGroup = await UserGroup.findOne({
                        where: {
                            groupId: groupId,
                            userId: detail.userId
                        }
                    });
                    let newBalance = userGroup.balance;

                    if (parseInt(detail.userId) !== parseInt(senderId)) {
                        newBalance = userGroup.balance - parseInt(detail.amount).toFixed(2);
                    } else {
                        newBalance = userGroup.balance + total_amount - parseInt(detail.amount).toFixed(2);
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
                    CreateNotif(detail.userId,`"Nouvelle dépense: " ${label}`,`groups/${groupId}`);
                }
            }
            await RefundController.calculateMinimalRefunds(groupId)
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
        const transaction = await Transaction.findByPk(req.params.transactionId, {
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

const getGroupTransactions = async (req, res) => {
    console.log(`REST getGroupTransaction`);
    try {
        const groupId = req.params.groupId;
        const userGroup = await UserGroup.findOne({
            where: {
                userId: req.authorization.userId,
                groupId,
                active: true
            }
        });
        if (!userGroup) {
            return res.status(404).send('User not found in group');
        }
        console.log(`userInGroup`);
        let transactions = await Transaction.findAll({
            where: {
                groupId
            },
            include: [{
                model: TransactionUser,
                attributes: ['userId', 'amount']
            }]
        });
        return res.status(200).send(transactions);
    } catch (e) {
        return res.status(400).send(e);
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

        transactions.map(obj => {

            const base_url = obj.Transaction.Group.picture;
            if (base_url){
                obj.Transaction.Group.picture = [base_url+"/100",base_url+"/200",base_url+"/500"]
            }else {
                obj.Transaction.Group.picture = null;
            }

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
    getGroupTransactions,
    getUserTransactions,
    getXLastsUserTransactions
}