const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//POST http://localhost:9002/api/group/create
router.post("/create", securityMiddleware.verifyIsAuth, GroupController.createGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/:groupId", securityMiddleware.verifyIsAuth, GroupController.modifyGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/:groupId/picture", securityMiddleware.verifyIsAuth, GroupController.modifyGroupPicture);

module.exports = router;
