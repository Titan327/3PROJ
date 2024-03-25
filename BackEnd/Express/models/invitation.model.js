const { DataTypes, Transaction} = require('sequelize');
const sequelize = require('../configurations/db.config');
const GroupModel = require('./group.model');

const InvitationModel = sequelize.define("Invitation", {
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
    remaining_uses: {
        type: DataTypes.INTEGER,
        allowNull: false
    },
    expiration_date: {
        type: DataTypes.DATE,
        allowNull: false
    },
    token: {
        type: DataTypes.STRING,
        allowNull: false
    },
});

// Relation avec les groupes
InvitationModel.belongsTo(GroupModel, { foreignKey: 'groupId' });

sequelize.sync().then(() => {
    console.log('InvitationModel table created successfully!');
}).catch((error) => {
    console.error('Unable to create table UserModel : InvitationModel', error);
});

module.exports = InvitationModel;