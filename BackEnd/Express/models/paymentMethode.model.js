const { DataTypes, Transaction} = require('sequelize');
const sequelize = require('../configurations/db.config');
const UserModel = require('./user.model');

const paymentMethodeModel = sequelize.define("PaymentMethode", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    userId: {
        type: DataTypes.BIGINT,
        allowNull: false
    },
    type :{
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            isIn: {
                args: [['Paypal email', 'RIB']],
                msg: "Type must be either 'Paypal', or 'RIB'"
            }
        }
    },
    value: {
        type: DataTypes.STRING,
        allowNull: false,
        validate: {
            /*
            isValid(value) {
                if (this.type === 'Paypal email') {
                    const emailRegex = /^[\w-]+(\.[\w-]+)*@([\w-]+\.)+[a-zA-Z]{2,7}$/;
                    if (!emailRegex.test(value)) {
                        throw new Error('Invalid Paypal email');
                    }
                } else if (this.type === 'RIB') {
                    const ribRegex = /^[A-Z]{2}[0-9]{2}[0-9A-Z]{23}$/;
                    if (!ribRegex.test(value)) {
                        throw new Error('Invalid RIB');
                    }
                }
            }
            */
        }
    },
});

// Relation avec les groupes
paymentMethodeModel.belongsTo(UserModel, { foreignKey: 'userId' });

sequelize.sync().then(() => {
    console.log('PaymentModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table PaymentModel : PaymentModel', error);
});

module.exports = paymentMethodeModel;