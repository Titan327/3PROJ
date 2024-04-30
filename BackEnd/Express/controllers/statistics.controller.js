const TransactionUser = require('../models/transactionUser.model');
const Transaction = require('../models/transaction.model');
const TransactionCategory = require('../models/transactionCategory.model');
const {Op} = require("sequelize");

const getAllStatistics = async (req, res) => {
    console.log(`REST getAllStatistics`);
    const userId = req.params.userId;
    try {
        const categoryStats = await getAllCategorystats(userId);
        const monthlyStats = await getAllMonthlyTransactionsOnAYear(userId);
        const averageTransactionPrice = await getAverageTransactionPrice(userId);
        const statistics = [
            averageTransactionPrice,
            {amountByCategories: categoryStats.amountByCategories},
            {numberByCategories: categoryStats.numberByCategories},
            {averageByCategories: categoryStats.averageByCategories},
            {amountByMonth: monthlyStats.amountByMonth},
            {numberByMonth: monthlyStats.numberByMonth},
            {averageByMonth: monthlyStats.averageByMonth}
        ];
        return res.status(200).send(statistics);
    } catch (e) {
        console.log(e);
        return res.status(500).send(e);
    }
}

const getAverageTransactionPrice = async (userId) => {
    console.log(`REST getAverageTransactionPrice`);
    try {
        const transactions = await TransactionUser.findAll({
            where: {
                userId
            },
            attributes: ['amount']
        });
        let total = 0;
        for (let i = 0; i < transactions.length; i++) {
            total += transactions[i].amount;
        }
        const average = parseFloat(total / transactions.length).toFixed(2);
        return {
            average: average,
            transactions: transactions.length
        };
    } catch (e) {
        return e;
    }
}

const getAllCategorystats = async (userId) => {
    console.log(`REST getAllTransactionsAmountByCategory`);
    try {
        const transactions = await TransactionUser.findAll({
            where: {
                userId
            },
            include: [{
                model: Transaction,
                include: [{
                    model: TransactionCategory
                }]
            }]
        });
        let amountByCategories = {};
        let numberByCategories = {};
        let averageByCategories = {};
        for (let i = 0; i < transactions.length; i++) {
            let category = transactions[i].Transaction.TransactionCategory.label;
            if (!amountByCategories[category]) {
                amountByCategories[category] = 0;
                numberByCategories[category] = 0;
            }
            amountByCategories[category] += transactions[i].amount;
            numberByCategories[category]++;
        }
        for (let category in amountByCategories) {
            averageByCategories[category] = parseFloat(amountByCategories[category] / numberByCategories[category]).toFixed(2);
        }
        return {
            amountByCategories,
            numberByCategories,
            averageByCategories
        };
    }
    catch (e) {
        return e;
    }
}

const getAllMonthlyTransactionsOnAYear = async (userId) => {
    console.log(`REST getAmountOfAllUserTransactionsByMonthOnLastYear`);
    try {
        const transactions = await TransactionUser.findAll({
            where: {
                userId,
                createdAt: {
                    [Op.gte]: new Date(new Date().setFullYear(new Date().getFullYear() - 1))
                }
            },
        });
        let amountByMonth = {};
        let numberByMonth = {};
        let averageByMonth = {};
        for (let i = 0; i < transactions.length; i++) {
            let month = transactions[i].createdAt.getMonth().toString() + "/" + transactions[i].createdAt.getFullYear().toString();
            if (!amountByMonth[month]) {
                amountByMonth[month] = 0;
                numberByMonth[month] = 0;
            }
            amountByMonth[month] += transactions[i].amount;
            numberByMonth[month]++;
        }
        for (let month in amountByMonth) {
            averageByMonth[month] = parseFloat(amountByMonth[month] / numberByMonth[month]).toFixed(2);
        }
        return {
            amountByMonth,
            numberByMonth,
            averageByMonth
        };
    } catch (e) {
        return e;
    }
}

module.exports = {
    getAllStatistics
};