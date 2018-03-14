### Introduce

The program contains simple erp, order management, after sale services, pay, message, privilege management, work flow etc functions. Use spring-cloud microservice with redis, activemq, hibernate, mysql cluster.


### Privileges notice

- 销售人员分配权限时需分配：/sys/audit/priceChange/saler 权限，以便销售人员可以发起：销售员调价申请
- 销售主管分配权限时需分配：/sys/audit/priceChange/charger 权限，以便销售主管可以发起：销售主管调价申请
- 销售经理分配权限时需分配：/sys/audit/priceChange/manager 权限，以便销售经理可以发起：销售经理调价申请
- 销售总监分配权限时需分配：/sys/audit/priceChange/director 权限，以便销售总监可以发起：销售总监调价申请
- 库存人员分配权限时需分配：/erp/print/expressWaybill 权限，以便库存人员可以打印快递单
