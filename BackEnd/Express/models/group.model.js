const { DataTypes } = require('sequelize');
const sequelize = require('../configurations/db.config');

const GroupModel = sequelize.define("Group", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    name: {
        type: DataTypes.STRING,
        allowNull: false
    },
    description: {
        type: DataTypes.STRING,
        allowNull: true
    },
    picture: {
        type: DataTypes.STRING,
        allowNull: true
    }
});

sequelize.sync().then(() => {
    console.log('GroupModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table GroupModel : ', error);
});


module.exports = GroupModel;