const router = require('express').Router();
const UserController = require('../controllers/user.controller');
const GroupController = require('../controllers/group.controller');
const TransactionController = require('../controllers/transaction.controller');
const securityMiddleware = require('../security/middleware.security');
const userMiddleware = require('../middlewares/user.middleware');
const RefundController = require('../controllers/refund.controller');
const PaymentMethodeController = require('../controllers/paymentMethode.controller');

//PUBLIC
//GET http://localhost:9002/api/users/{id}/groups
router.get("/:userId/groups", securityMiddleware.verifyIsAuthAndActivUser, GroupController.getGroups);
//GET http://localhost:9002/api/users/{id}/transactions
router.get("/:userId/transactions", securityMiddleware.verifyIsAuthAndActivUser, TransactionController.getUserTransactions);
//GET http://localhost:9002/api/users/{id}/lastTransactions/limit={limit}
router.get("/:userId/lastTransactions", securityMiddleware.verifyIsAuthAndActivUser, TransactionController.getXLastsUserTransactions);
//GET http://localhost:9002/api/users/{id}/transnactions/thisMonth
router.get("/:userId/transactions/thisMonth", securityMiddleware.verifyIsAuthAndActivUser, UserController.getAmountOfAllUserTransactionsThisMonth);
//GET http://localhost:9002/api/users/{id}/transnactions/averageBalance
router.get("/:userId/transactions/totalBalance", securityMiddleware.verifyIsAuthAndActivUser, UserController.totalBalance);


//GET http://localhost:9002/api/users/{id}
router.get("/:userId", securityMiddleware.verifyIsAuthAndActivUser, UserController.getUser);
//PUT http://localhost:9002/api/users/{id}
router.put("/:userId", securityMiddleware.verifyIsAuthAndActivUser, userMiddleware.verifyUserInfos, UserController.modifyUser);
//PUT http://localhost:9002/api/users/{id}/password
router.put("/:userId/password", securityMiddleware.verifyIsAuthAndActivUser, userMiddleware.verifyPasswordForSensibleInfos, userMiddleware.verifyPasswordWhenCreatingOrUpdating, UserController.modifyPassword);
//DELETE http://localhost:9002/api/users/{id}
router.delete("/:userId", securityMiddleware.verifyIsAuthAndActivUser, userMiddleware.verifyPasswordForSensibleInfos, UserController.deleteUser);
router.post("/:userId/paymentMethode", securityMiddleware.verifyIsAuthAndActivUser, PaymentMethodeController.createPaymentMethode);
router.get("/:userId/:groupId/paymentMethode", securityMiddleware.verifyIsAuthAndActivUser, PaymentMethodeController.getPaymentMethode);
router.get("/me/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.getMyPaymentMethode);


module.exports = router;
