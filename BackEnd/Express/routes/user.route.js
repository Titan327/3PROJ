const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//GET http://localhost:9002/api/user/{id}/groups
router.get("/:userId/groups", securityMiddleware.verifyIsAuthAndActivUser, GroupController.getGroups);

module.exports = router;
