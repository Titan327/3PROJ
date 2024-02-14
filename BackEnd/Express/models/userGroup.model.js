const { DataTypes } = require('sequelize');
const sequelize = require('../configurations/db.config');
const Group = require('./group.model');
const User = require('./user.model');

const UserGroupModel = sequelize.define("UserGroup", {
    id : {
        type: DataTypes.BIGINT,
        primaryKey: true,
        autoIncrement: true,
        allowNull: false
    },
    user_id: {
        type: DataTypes.BIGINT,
        allowNull: false,
        references: {
            model: User,
            key: 'id'
        }
    },
    group_id: {
        type: DataTypes.BIGINT,
        allowNull: false,
        references: {
            model: Group,
            key: 'id'
        }
    },
    balance: {
        type: DataTypes.FLOAT,
        allowNull: false,
        defaultValue: 0
    }
});

// Relation Many-to-Many avec l'entité User et Group
User.belongsToMany(Group, { through: UserGroupModel, foreignKey: 'user_id' });
Group.belongsToMany(User, { through: UserGroupModel, foreignKey: 'group_id' });

sequelize.sync().then(() => {
    console.log('UserGroupModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table UserGroupModel : ', error);
});

module.exports = UserGroupModel;