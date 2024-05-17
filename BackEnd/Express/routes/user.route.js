const router = require('express').Router();
const UserController = require('../controllers/user.controller');
const GroupController = require('../controllers/group.controller');
const TransactionController = require('../controllers/transaction.controller');
const securityMiddleware = require('../security/middleware.security');
const userMiddleware = require('../middlewares/user.middleware');
const RefundController = require('../controllers/refund.controller');
const PaymentMethodeController = require('../controllers/paymentMethode.controller');
const StatisticsController = require('../controllers/statistics.controller');

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
router.post("/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.createPaymentMethode);
router.delete("/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.deletePaymentMethode);
router.get("/:userId/:groupId/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.getPaymentMethode);
router.get("/me/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.getMyPaymentMethode);

//GET http://localhost:9002/api/users/{id}/statistics
router.get("/:userId/statistics", securityMiddleware.verifyIsAuthAndActivUser, StatisticsController.getAllStatistics);


module.exports = router;
