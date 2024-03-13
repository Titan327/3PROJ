const router = require('express').Router();
const UserController = require('../controllers/user.controller');
const GroupController = require('../controllers/group.controller');
const TransactionController = require('../controllers/transaction.controller');
const securityMiddleware = require('../security/middleware.security');
const userMiddleware = require('../middlewares/user.middleware');
const RefundController = require('../controllers/refund.controller');

//PUBLIC
//GET http://localhost:9002/api/user/{id}/groups
router.get("/:userId/groups", securityMiddleware.verifyIsAuthAndActivUser, GroupController.getGroups);
//GET http://localhost:9002/api/user/{id}/transactions
router.get("/:userId/transactions", securityMiddleware.verifyIsAuthAndActivUser, TransactionController.getUserTransactions);
//GET http://localhost:9002/api/user/{id}/lastTransactions/limit={limit}
router.get("/:userId/lastTransactions", securityMiddleware.verifyIsAuthAndActivUser, TransactionController.getXLastsUserTransactions);
//POST http://localhost:9002/api/user/{id}/refund
router.post("/:userId/refund", securityMiddleware.verifyIsAuthAndActivUser, RefundController.refundTransactions);


//GET http://localhost:9002/api/user/{id}
router.get("/:userId", securityMiddleware.verifyIsAuthAndActivUser, UserController.getUser);
//PUT http://localhost:9002/api/user/{id}
router.put("/:userId", securityMiddleware.verifyIsAuthAndActivUser, userMiddleware.verifyUserInfos, userMiddleware.verifyPasswordForSensibleInfos, UserController.modifyUser);
//DELETE http://localhost:9002/api/user/{id}
router.delete("/:userId", securityMiddleware.verifyIsAuthAndActivUser, userMiddleware.verifyPasswordForSensibleInfos, UserController.deleteUser);


module.exports = router;
