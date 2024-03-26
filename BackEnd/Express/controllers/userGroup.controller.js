const UserGroup = require('../models/userGroup.model');
const User = require("../models/user.model");

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

const verifyUserInGroup = async (userId,groupId) => {
    try{
        const user = await UserGroup.findOne({
            where: {
                userId: userId,
                groupId:groupId
            },
        });
        return !!user;
    }catch (e){
        console.error(e);
        return e;
    }

}

module.exports = {
    createUserGroupRelation,
    deleteUserGroupRelationsWhenDeletingUser,
    verifyUserInGroup
}