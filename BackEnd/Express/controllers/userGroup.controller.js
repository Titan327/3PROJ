const UserGroup = require('../models/userGroup.model');

const createUserGroupRelation = async (userId, group_id) => {
    console.log(`REST createUserGroupRelation`);
    try {
    return await UserGroup.create({
        userId,
        group_id
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