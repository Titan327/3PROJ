const Notif = require('../models/notif.model');
const UserGroup = require("../models/userGroup.model");
const Message = require("../models/message.model");

async function CreateNotif (user_id,message,link) {

    await Notif.create(
        {
            user_id: user_id,
            message: message,
            link: link,
            seen: false
        }
    )

}

const GetAllNotifByUser = async (req,res) => {
    try {
        const userId = req.authorization.userId;

        const allNotif = await Notif.find({user_id: userId,seen: 0});

        await Notif.updateMany({user_id: userId,seen: 0},{seen: true});

        return res.status(200).json({allNotif});
    }catch (e){
        return res.status(500).json({e});
    }
}

const GetNumNotifByUser = async (req,res) => {
    try {
        const userId = req.authorization.userId;

        const num = await Notif.countDocuments({user_id: userId,seen: 0});

        return res.status(200).json({num});
    }catch (e){
        return res.status(500).json({e});
    }
}

module.exports={
    CreateNotif,
    GetAllNotifByUser,
    GetNumNotifByUser
}