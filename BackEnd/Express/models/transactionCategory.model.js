const { DataTypes } = require('sequelize');
const sequelize = require('../configurations/db.config');

const TransactionCategoryModel = sequelize.define("TransactionCategory", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    label: {
        type: DataTypes.STRING,
        allowNull: false
    },
});

sequelize.sync().then(() => {
    console.log('TransactionCategoryModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table TransactionCategoryModel : ', error);
});

module.exports = TransactionCategoryModel;