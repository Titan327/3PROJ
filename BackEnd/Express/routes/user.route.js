const router = require('express').Router();
const GroupController = require('../controllers/group.controller');
const TransactionController = require('../controllers/transaction.controller');
const securityMiddleware = require('../security/middleware.security');

//PUBLIC
//GET http://localhost:9002/api/user/{id}/groups
router.get("/:userId/groups", securityMiddleware.verifyIsAuthAndActivUser, GroupController.getGroups);
//GET http://localhost:9002/api/user/{id}/transactions
router.get("/:userId/transactions", securityMiddleware.verifyIsAuthAndActivUser, TransactionController.getUserTransactions);

module.exports = router;
