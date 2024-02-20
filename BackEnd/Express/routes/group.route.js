const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//POST http://localhost:9002/api/group/create
router.post("/create", securityMiddleware.verifyIsAuth, GroupController.createGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/group/:groupId", securityMiddleware.verifyIsAuth, GroupController.modifyGroup);

module.exports = router;
