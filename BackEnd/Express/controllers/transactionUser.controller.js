const TransactionUser = require('../models/transactionUser.model');
const {Op} = require("sequelize");

const getAllNotRefundedTransactionsByUser = async (userId) => {

    try {
        const transactions = await TransactionUser.findAll({
            where: { refunded: false , userId: userId }
        });
        return transactions;
    } catch (e) {
        console.error(e);
        return e;
    }
}

const getAllUserTransactionsThisMonth = async (userId) => {

    try {
        const transactions = await TransactionUser.findAll({
            where: { userId: userId, createdAt: { [Op.gte]: new Date(new Date().getFullYear(), new Date().getMonth(), 1) } }
        });
        return transactions;
    } catch (e) {
        console.error(e);
        return e;
    }

}

module.exports = {
    getAllNotRefundedTransactionsByUser,
    getAllUserTransactionsThisMonth
}