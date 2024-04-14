const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const RefundController = require('../controllers/refund.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//GET http://localhost:9002/api/group/{id}
router.get("/:groupId", securityMiddleware.verifyIsAuth ,GroupController.getGroup);
//POST http://localhost:9002/api/group/create
router.post("/create", securityMiddleware.verifyIsAuth, GroupController.createGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/:groupId", securityMiddleware.verifyIsAuth, GroupController.modifyGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/:groupId/picture", securityMiddleware.verifyIsAuth, GroupController.modifyGroupPicture);

//GET http://localhost:9002/api/group/{id}/user/{id}/debts
router.get("/:groupId/user/:userId/debts", securityMiddleware.verifyIsAuth, RefundController.getUserDebtForOtherGroupsUsers);

module.exports = router;
