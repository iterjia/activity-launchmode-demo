## activity-launchmode-demo
里面包含两个module，app和another。
app有三个Activity，分别是A、B、C。
another有三个Activity，分别是D、E、F。
通过调整类中getTargetTag方法中的目标值和修改AndroidManifest文件中Activity对应的LaunchMode属性，可以任意组合跳转方式进行验证。

# ABC=standard
[A->B->C->A1->B1->C1] 同一任务栈，不同实例，逐个返回

# ABC=standard, B.taskAffinity单独设置
[A->B->C->A1->B1->C1] 同一任务栈，不同实例，逐个返回。standard时taskAffinity设置无效

# AC=standard, B=singleTask
[A->B->C->A1->B] 同一任务栈，从A1启动B时，B到栈顶，A1和C被销毁，变为[A->B]

# AC=standard, B=singleTask && taskAffinity单独设置
[A]->[B->C->A1->B] 不同任务栈，从A1启动B时，B到栈顶，A1和C被销毁，变为[A]->[B]

# AC=standard, B=singleTask && taskAffinity单独设置, E=singleTask
[A]->[B->C]->[E]->[B] 不同任务栈，EB不同进程；从E启动B时，B到栈顶，C被销毁，变为[A]->[E]->[B]

# A=singleInstance, CD=standard || A=singleInstance, C=singleTop, D=standard
[A]->[C->D]->[A]->C 第二次从A启动C时，CD任务栈到顶部，显示D（此时没有触发onNewIntent事件）

# A=singleInstance, C=singleTask, D=standard
[A]->[C->D]->[A]->C 第二次从A启动C时，D被销毁，C到栈顶

# 总结：
## launchMode=singleInstance时系统中只有一个实例，由此启动的Activity都会创建新的任务栈。
## launchMode=singleTask时系统中只有一个实例，该特性决定了在启动该Activity时，在其顶部的Activity会被销毁。任务栈的情况配合taskAffinity属性有不同的结果，taskAffinity属性值默认是包名。如果启动它的Activity与其具有相同的taskAffinity属性，则在同一个任务栈中，否则会创建新的任务栈
