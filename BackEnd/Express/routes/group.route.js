const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const UserGroupController = require('../controllers/userGroup.controller');
const securityMiddleware = require('../security/middleware.security');
const PaymentMethodeController = require('../controllers/paymentMethode.controller');
const TransactionController = require('../controllers/transaction.controller');
const RefundController = require('../controllers/refund.controller');

//PUBLIC
//GET http://localhost:9002/api/groups/{id}
router.get("/:groupId", securityMiddleware.verifyIsAuth ,GroupController.getGroup);
//POST http://localhost:9002/api/groups/
router.post("/", securityMiddleware.verifyIsAuth, GroupController.createGroup);
//PUT http://localhost:9002/api/groups/{id}
router.put("/:groupId", securityMiddleware.verifyIsAuth, GroupController.modifyGroup);
//PUT http://localhost:9002/api/groups/{id}
router.put("/:groupId/picture", securityMiddleware.verifyIsAuth, GroupController.modifyGroupPicture);
//PUT http://localhost:9002/api/groups/{id}/favorite
router.put("/:groupId/favorite", securityMiddleware.verifyIsAuth, UserGroupController.setFavorite);

//GET http://localhost:9002/api/groups/{id}/users/{id}/paymentMethode
router.get("/:groupId/users/:userId/paymentMethode", securityMiddleware.verifyIsAuth, PaymentMethodeController.getPaymentMethode);

//GET http://localhost:9002/api/groups/refunds
router.get("/:groupId/refunds", securityMiddleware.verifyIsAuth, RefundController.getGroupRefunds);
//GET http://localhost:9002/api/groups/doneRefunds
router.get("/:groupId/refunds/done", securityMiddleware.verifyIsAuth, RefundController.getGroupDoneRefunds);
//POST http://localhost:9002/api/groups/{groupId}/refund/{refundId}
router.post("/:groupId/refund/:refundId", securityMiddleware.verifyIsAuth, RefundController.processRefund);

//GET http://localhost:9002/api/groups/transactions
router.get("/:groupId/transactions", securityMiddleware.verifyIsAuth, TransactionController.getGroupTransactions);
//GET http://localhost:9002/api/groups/{id}transactions/{id}
router.get("/:groupId/transactions/:transactionId", securityMiddleware.verifyIsAuth, TransactionController.getTransaction);
//POST http://localhost:9002/api/groups/{id}transaction
router.post("/:groupId/transactions", securityMiddleware.verifyIsAuth, TransactionController.createTransaction);

module.exports = router;
