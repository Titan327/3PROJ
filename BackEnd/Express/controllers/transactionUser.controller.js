const TransactionUser = require('../models/transactionUser.model');
const {Op} = require("sequelize");

const getAllNotRefoundedTransactionsByUser = async (userId) => {
    console.log(`REST getAllNotRefoundedTransactionsByUser`);
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
    console.log(`REST getAllUserTransactionsThisMonth`);
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
    getAllNotRefoundedTransactionsByUser,
    getAllUserTransactionsThisMonth
}