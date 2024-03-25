const { DataTypes, Transaction} = require('sequelize');
const sequelize = require('../configurations/db.config')
const TransactionUserModel = require('./transactionUser.model');
const UserModel = require('./user.model');

const RefundModel = sequelize.define("Refund", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    transactionId: {
        type: DataTypes.BIGINT,
        allowNull: false
    },
    refundingUserId: {
        type: DataTypes.BIGINT,
        allowNull: false
    },
    refundedUserId: {
        type: DataTypes.BIGINT,
        allowNull: false
    },
    amount: {
        type: DataTypes.FLOAT,
        allowNull: false
    },
});

// Relation avec les users & userTransaction
RefundModel.belongsTo(TransactionUserModel, { foreignKey: 'transactionId' });
RefundModel.belongsTo(UserModel, { foreignKey: 'refundingUserId' });
RefundModel.belongsTo(UserModel, { foreignKey: 'refundedUserId' });

sequelize.sync().then(() => {
    console.log('RefundModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table RefundModel : RefundModel', error);
});

module.exports = RefundModel;