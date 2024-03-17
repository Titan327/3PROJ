const UserGroup = require('../models/userGroup.model');

const createUserGroupRelation = async (userId, groupId) => {
    console.log(`REST createUserGroupRelation`);
    try {
    return await UserGroup.create({
        userId,
        groupId: groupId
    });
    } catch (e) {
        console.error(e);
        return e;
    }
}

const deleteUserGroupRelationsWhenDeletingUser = async (userId) => {
    console.log(`REST deleteUserGroupRelationsWhenDeletingUser`);
    try {
        await UserGroup.destroy({
            where: { userId }
        });
    } catch (e) {
        console.error(e);
        return e;
    }
}

module.exports = {
    createUserGroupRelation,
    deleteUserGroupRelationsWhenDeletingUser
}