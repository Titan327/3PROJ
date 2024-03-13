const Group = require('../models/group.model');
const UserGroup = require('../models/userGroup.model');
const Joi = require('joi');
const {createUserGroupRelation} = require("./userGroup.controller");
const {Op} = require("sequelize");

const getGroups = async (req, res) => {
    console.log(`REST getGroups`);
    if (!req.params.userId) {
        return res.status(400).send({ message: "User ID is required" });
    }
    try {
        const userId = req.params.userId;
        const limit = parseInt(req.query.limit) || 5;
        let whereCondition = {userId: userId};

        if (req.query.favorite === "true") {
            whereCondition.favorite = true;
        } else if (req.query.favorite === "false") {
            whereCondition.favorite = false;
        }

        const groupsID = await UserGroup.findAll({where: whereCondition});
        if (groupsID.length > 0) {
            let groupIds = groupsID.map(group => group.groupId);
            console.log(`groupIds: ${groupIds}`);
            let groups = await Group.findAll({where: {id: groupIds}, order: [['updatedAt', 'ASC']], limit: limit});
            return res.status(200).send(groups);
        }
        return res.status(404).send({ message: "No groups found" });
    } catch (error) {
        console.error(error);
        return res.status(500).send({ message: "Internal Server Error" });
    }
}

const createGroup = async (req, res) => {
    console.log(`REST createGroup`);
    const schema = Joi.object({
        name: Joi.string().min(3).max(63).required(),
        description: Joi.string().min(3).max(255),
    });
    const { error, value } = schema.validate(req.body, { abortEarly: false });
    const { name, description } = value;
    if (error) {
        const errorMessage = error.details.map(detail => detail.message.replace(/"/g, ''));
        return res.status(400).json({ error: errorMessage });
    }
    try {
        const userId = req.authorization.userId;
        console.log(`userId: ${userId}`);
        // Stocker le groupe créé dans une variable
        let newGroup = await Group.create({
            name,
            description,
            ownerId: userId,
        });
        console.log(`newGroup: ${newGroup.id}`);
        await createUserGroupRelation(userId, newGroup.id);
        res.status(201).send({ message: "Group created successfully", group: newGroup });
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const modifyGroup = async (req, res) => {
    console.log(`REST modifyGroup`);
    const { groupId } = req.params;
    const { name, description } = req.body;
    try {
        const userId = req.authorization.userId;
        let group = await Group.findOne({where: {id: groupId}});
        if (group === null) {
            return res.status(404).send({ error: "Group not found" });
        } else if (group.ownerId !== userId) {
            return res.status(403).send({ error: "You are not the owner of this group" });
        } else {
            await Group.update({ name, description }, { where: { id: groupId } });
            return res.status(200).send({ message: "Group modified successfully" });
        }
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const modifyGroupPicture = async (req, res) => {
    console.log(`REST modifyGroup`);
    const { groupId } = req.params;
    const { url } = req.body;
    try {
        const userId = req.authorization.userId;
        let group = await Group.findOne({where: {id: groupId}});
        if (group === null) {
            return res.status(404).send({ error: "Group not found" });
        } else if (group.ownerId !== userId) {
            return res.status(403).send({ error: "You are not the owner of this group" });
        } else {
            await Group.update({ picture: url }, { where: { id: groupId } });
            return res.status(200).send({ message: "Group picture modified successfully" });
        }
    } catch (e) {
        console.error(e);
        res.status(500).send(e);
    }
}

const switchGroupOwnerWhenDeletingUser = async (userId) => {
    console.log(`REST switchGroupOwnerWhenDeletingUser`);
    try {
        let groups = await Group.findAll({where: {ownerId: userId}});
        if (groups.length > 0) {
            for (let group of groups) {
                let newOwner = await UserGroup.findOne({
                    where: {
                        groupId: group.id,
                        userId: {
                            [Op.ne]: userId
                        },
                        active: true
                    }
                });
                if (newOwner) {
                    await Group.update({ ownerId: newOwner.userId }, { where: { id: group.id } });
                } else {
                    await Group.destroy({ where: { id: group.id } });
                }
            }
        }
    } catch (e) {
        console.error(e);
        return e;
    }
}

module.exports = {
    createGroup,
    modifyGroup,
    getGroups,
    modifyGroupPicture
}