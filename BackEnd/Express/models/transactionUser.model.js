const { DataTypes } = require('sequelize');
const sequelize = require('../configurations/db.config');
const Transaction = require('./transaction.model');
const User = require('./user.model');

const TransactionUserModel = sequelize.define("TransactionUser", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    userId: {
        type: DataTypes.BIGINT,
        allowNull: false,
        references: {
            model: User,
            key: 'id'
        }
    },
    transactionId: {
        type: DataTypes.BIGINT,
        allowNull: false,
        references: {
            model: Transaction,
            key: 'id'
        }
    },
    amount: {
        type: DataTypes.FLOAT,
        allowNull: false
    }
});

// Relation Many-to-Many avec l'entité User et Group
User.belongsToMany(Transaction, { through: TransactionUserModel, foreignKey: 'userId' });
Transaction.belongsToMany(User, { through: TransactionUserModel, foreignKey: 'transactionId' });
TransactionUserModel.belongsTo(Transaction, { foreignKey: 'transactionId' });
Transaction.hasMany(TransactionUserModel, { foreignKey: 'transactionId' }); // Added this line

sequelize.sync().then(() => {
    console.log('TransactionUser table created successfully!');
}).catch((error) => {
    console.error('Unable to create table TransactionUser : ', error);
});

module.exports = TransactionUserModel;