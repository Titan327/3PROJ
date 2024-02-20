const Group = require('../models/group.model');
const UserGroup = require('../models/userGroup.model');
const Joi = require('joi');
const {createUserGroupRelation} = require("./userGroup.controller");

const getGroups = async (req, res) => {
    console.log(`REST getGroups`);
    if (!req.params.userId) {
        return res.status(400).send({ message: "User ID is required" });
    }
    try {
        const user_id = req.params.userId;
        const groupsID = await UserGroup.findAll({where: {user_id: user_id}});
        console.log(`groupsID: ${groupsID}`);
        if (groupsID.length > 0) {
            let groupIds = groupsID.map(group => group.group_id);
            console.log(`groupIds: ${groupIds}`);
            let groups = [];
            groups = await Group.findAll({where: {id: groupIds}});
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
        let user_id = req.authorization.user_id;
        console.log(`user_id: ${user_id}`);
        // Stocker le groupe créé dans une variable
        let newGroup = await Group.create({
            name,
            description,
            owner_id: user_id,
        });
        createUserGroupRelation(user_id, newGroup.id);
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
        const user_id = req.authorization.user_id;
        let group = await Group.findOne({where: {id: groupId}});
        if (group === null) {
            return res.status(404).send({ error: "Group not found" });
        } else if (group.owner_id !== user_id) {
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
        const user_id = req.authorization.user_id;
        let group = await Group.findOne({where: {id: groupId}});
        if (group === null) {
            return res.status(404).send({ error: "Group not found" });
        } else if (group.owner_id !== user_id) {
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

module.exports = {
    createGroup,
    modifyGroup,
    getGroups,
    modifyGroupPicture
}