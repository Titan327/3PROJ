const UserGroup = require('../models/userGroup.model');

const createUserGroupRelation = async (user_id, group_id) => {
    console.log(`REST createUserGroupRelation`);
try {
    return await UserGroup.create({
        user_id,
        group_id
    });
    } catch (e) {
        console.error(e);
        return e;
    }
}

module.exports = {
    createUserGroupRelation
}