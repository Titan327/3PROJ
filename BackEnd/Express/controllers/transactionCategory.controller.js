const TransactionCategory = require('../models/transactionCategory.model');

const getAllTrasactionCategory = async (req, res) => {
    try {
        const Category = await TransactionCategory.findAll({
            attributes: ['id','label']
        });
        return res.status(200).send(Category);
    } catch (e) {
        console.error(e);
        return e;
    }
}


module.exports = {
    getAllTrasactionCategory
}