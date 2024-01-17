const { DataTypes } = require('sequelize');
const sequelize = require('../configurations/db.config');

const UserModel = sequelize.define("Users", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    firstname: {
        type: DataTypes.STRING,
        allowNull: false
    },
    lastname: {
        type: DataTypes.STRING,
        allowNull: false
    },
    email: {
        type: DataTypes.STRING,
        allowNull: false
    },
    verified_email: {
        type: DataTypes.BOOLEAN,
        allowNull: false
    },
    birth_date: {
        type: DataTypes.DATE,
        allowNull: true
    },
    password: {
        type: DataTypes.STRING,
        allowNull: false
    },
    profile_picture: {
        type: DataTypes.STRING,
        allowNull: true
    },
});




sequelize.sync().then(() => {
    console.log('UserModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table UserModel : ', error);
});

module.exports = UserModel;