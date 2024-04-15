const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const UserGroupController = require('../controllers/userGroup.controller');
const RefundController = require('../controllers/refund.controller');
const securityMiddleware = require('../security/middleware.security');
const PaymentMethodeController = require('../controllers/paymentMethode.controller');

//PUBLIC
//GET http://localhost:9002/api/group/{id}
router.get("/:groupId", securityMiddleware.verifyIsAuth ,GroupController.getGroup);
//POST http://localhost:9002/api/group/
router.post("/", securityMiddleware.verifyIsAuth, GroupController.createGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/:groupId", securityMiddleware.verifyIsAuth, GroupController.modifyGroup);
//PUT http://localhost:9002/api/group/{id}
router.put("/:groupId/picture", securityMiddleware.verifyIsAuth, GroupController.modifyGroupPicture);
//PUT http://localhost:9002/api/group/{id}/favorite
router.put("/:groupId/favorite", securityMiddleware.verifyIsAuth, UserGroupController.setFavorite);

//GET http://localhost:9002/api/group/{id}/user/{id}/debts
router.get("/:groupId/user/:userId/debts", securityMiddleware.verifyIsAuth, RefundController.getUserDebtForOtherGroupsUsers);
//GET http://localhost:9002/api/group/{id}/user/{id}/paymentMethode
router.get("/:groupId/user/:userId/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.getPaymentMethode);

module.exports = router;
