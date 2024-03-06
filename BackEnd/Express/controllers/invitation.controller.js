const {createUserGroupRelation} = require("./userGroup.controller");
const Invitation = require("../models/invitation.model");
const UserGroup = require("../models/userGroup.model");
const crypto = require('crypto');

const createInvitation = async (req, res) => {
    try {
        console.log('REST createInvitation');
        const userId = req.authorization.userId;
        console.log(`userId: ${userId}`);
        if (await UserGroup.findOne({where: {userId: userId, groupId: req.params.groupId}}) === null) {
            return res.status(403).send({error: "You are not allowed to create an invitation for this group"});
        }
        const groupId = req.params.groupId;
        const {remaining_uses, expiration_date } = req.body;

        const token = crypto.randomBytes(20).toString('hex');

        let newInvitation = await Invitation.create({
            groupId,
            remaining_uses,
            expiration_date,
            token
        });

        // Envoyer une réponse avec l'invitation créée
        return res.status(201).send({ message: "Invitation created successfully", invitation: newInvitation });
    } catch (e) {
        console.error(e);
        return res.status(500).send(e);
    }
}


const joinGroup = async (req, res) => {
    console.log('REST joinGroup');

    const token = req.params.token;
    const userId = req.authorization.userId;
    
    const invitation = await Invitation.findOne({where: {token: token}});
    if (invitation === null) {
        return res.status(404).send({error: "Invitation not found"});
    } else {
        if (invitation.expiration_date < Date.now()) {
            return res.status(403).send({error: "Invitation expired"});
        }
        if (invitation.remaining_uses === 0) {
            return res.status(403).send({error: "No more uses for this invitation"});
        }
        const userGroup = await UserGroup.findOne({where: {userId: userId, groupId: invitation.groupId}});
        if (userGroup !== null) {
            if (userGroup.active === true) {
                return res.status(403).send({error: "You are already in this group"});
            } else {
                userGroup.active = true;
                await userGroup.save();
            }
        } else {
            await createUserGroupRelation(userId, invitation.groupId);
        }
        invitation.remaining_uses--;
        await invitation.save();

        return res.status(200).send({message: "You have joined the group"});
    }
}

module.exports = {
    createInvitation,
    joinGroup
}