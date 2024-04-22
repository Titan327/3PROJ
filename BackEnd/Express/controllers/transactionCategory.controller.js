const TransactionCategory = require('../models/transactionCategory.model');

const getTrasactionCategories = async (req, res) => {
    try {
        const Category = await TransactionCategory.findAll({
            attributes: ['id','label','icon','color']
        });
        return res.status(200).send(Category);
    } catch (e) {
        console.error(e);
        return e;
    }
}


module.exports = {
    getTrasactionCategories
}