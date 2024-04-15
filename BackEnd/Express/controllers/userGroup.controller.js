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

const setFavorite = async (req, res) => {
    console.log(`REST setFavorite`);
    try {
        const userId = req.authorization.userId;
        const groupId = req.params.groupId;
        const { favorite } = req.body;
        const userGroup = await UserGroup.findOne({
            where: {
                userId,
                groupId,
                active: true
            }
        });
        if (!userGroup) {
            return res.status(404).send('User not found in group');
        }
        userGroup.favorite = favorite;
        await userGroup.save();
        return res.status(200).send(userGroup);
    } catch (e) {
        return res.status(400).send(e);
    }
}

module.exports = {
    createUserGroupRelation,
    deleteUserGroupRelationsWhenDeletingUser,
    verifyUserInGroup,
    setFavorite
}