const { minioClient } = require('../configurations/minio.config');
const User = require("../models/user.model");
const Group = require("../models/group.model");
const {verifyUserInGroup} = require("./userGroup.controller");

const uploadUserPic = async (req, res) => {

    try {
        const tokenValue = req.authorization
        const userId = tokenValue.userId

        if (!req.resizedImages) {
            return res.status(400).json({ error: 'Aucune image redimensionnée trouvée' });
        }

        req.resizedImages.forEach(image => {

            /*
            const metaData = {
                'Content-Type': 'jpg',
            };
            */
            minioClient.putObject("pp-user", userId+'/'+image.name, image.buffer);
        });

        const existing = await User.findOne({
            where: {
                id: userId,
            },
        });
        existing.profile_picture = process.env.APP_URL+"api/img/profile-picture/"+userId;
        await existing.save();

        res.status(200).json({ message: 'photo de profile upload' });
    }catch (e){
        res.status(500).json({ error: 'Erreur lors du redimensionnement des images' });

    }
}

const GetUserPic = async (req, res) => {
    const bucketName = 'pp-user';
    const userId = req.params.userId;
    const size = req.params.size;
    const existing = await User.findOne({
        where: {
            id: userId,
        },
    });

    if (existing){
        await minioClient.getObject(bucketName, userId+'/'+size, (err, dataStream) => {
            if (err) {
                return res.status(500).json({ error: "Photo introuvable" });
            }
            dataStream.pipe(res);
        });
    }else {
        return res.status(449).json({ error: "Utlisateur innexistante" });
    }
}


const uploadGroupPic = async (req, res) => {

    try {
        const tokenValue = req.authorization
        const groupId = req.params.groupId


        if (await verifyUserInGroup(tokenValue.userId,groupId)){
            if (!req.resizedImages) {
                return res.status(400).json({ error: 'Aucune image redimensionnée trouvée' });
            }

            req.resizedImages.forEach(image => {

                minioClient.putObject("pp-group", groupId+'/'+image.name, image.buffer);
            });

            const existing = await Group.findOne({
                where: {
                    id: groupId,
                },
            });
            existing.picture = process.env.APP_URL+"api/img/group-picture/"+groupId;
            await existing.save();

            return res.status(200).json({ message: 'photo de groupe upload' });
        }else {
            return res.status(403).send({error: "You are not allowed to change the image of this group"});
        }


    }catch (e){
        return res.status(500).json({ error: 'Erreur interne' });
    }
}

const GetGroupPic = async (req, res) => {
    try {
        const bucketName = 'pp-group';
        const groupId = req.params.groupId;
        const size = req.params.size;
        const existing = await Group.findOne({
            where: {
                id: groupId,
            },
        });

        if (existing){
            await minioClient.getObject(bucketName, groupId+'/'+size, (err, dataStream) => {
                if (err) {
                    return res.status(500).json({ error: "Photo introuvable" });
                }
                dataStream.pipe(res);
            });
        }else {
            return res.status(449).json({ error: "groupe innexistant" });
        }
    }catch (e){
        return res.status(500).json({ error: 'Erreur interne' });
    }
}

module.exports={
    uploadUserPic,
    GetUserPic,
    uploadGroupPic,
    GetGroupPic
}