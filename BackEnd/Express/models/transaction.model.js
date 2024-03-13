const { DataTypes, Transaction} = require('sequelize');
const sequelize = require('../configurations/db.config');
const TransactionCategoryModel = require('./transactionCategory.model');
const GroupModel = require('./group.model');

const TransactionModel = sequelize.define("Transaction", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    groupId: {
        type: DataTypes.BIGINT,
        allowNull: false
    },
    label: {
        type: DataTypes.STRING,
        allowNull: false
    },
    total_amount: {
        type: DataTypes.FLOAT,
        allowNull: false
    },
    date: {
        type: DataTypes.DATE,
        allowNull: false
    },
    receipt: {
        type: DataTypes.STRING,
        allowNull: false
    },
    senderId: {
        type: DataTypes.BIGINT,
        allowNull: false
    },
    categoryId: {
        type: DataTypes.BIGINT,
        allowNull: false
    }
});

// Relation entre les transactions et les groupes / catégories
TransactionModel.belongsTo(GroupModel, { foreignKey: 'groupId' });
TransactionModel.belongsTo(TransactionCategoryModel, { foreignKey: 'categoryId' });

sequelize.sync().then(() => {
    console.log('TransactionModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table UserModel : TransactionModel', error);
});

module.exports = TransactionModel;