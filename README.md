## activity-launchmode-demo
里面包含两个module，app和another。<br>
app有三个Activity，分别是A、B、C。<br>
another有三个Activity，分别是D、E、F。<br>
通过调整类中getTargetTag方法中的目标值和修改AndroidManifest文件中Activity对应的LaunchMode属性，可以任意组合跳转方式进行验证。
※可使用adb shell dumpsys activity activities命令查看详细信息

### ABC=standard
[A->B->C->A1->B1->C1] 同一任务栈，不同实例，逐个返回

### ABC=standard, B.taskAffinity单独设置
[A->B->C->A1->B1->C1] 同一任务栈，不同实例，逐个返回。standard时taskAffinity设置无效

### AC=standard, B=singleTask
[A->B->C->A1->B] 同一任务栈，从A1启动B时，B到栈顶，A1和C被销毁，变为[A->B]

### AC=standard, B=singleTask && taskAffinity单独设置
[A]->[B->C->A1->B] 不同任务栈，从A1启动B时，B到栈顶，A1和C被销毁，变为[A]->[B]

### AC=standard, B=singleTask && taskAffinity单独设置, E=singleTask
[A]->[B->C]->[E]->[B] 不同任务栈，EB不同进程；从E启动B时，B到栈顶，C被销毁，变为[A]->[E]->[B]

### A=singleInstance, CD=standard || A=singleInstance, C=singleTop, D=standard
[A]->[C->D]->[A]->C 第二次从A启动C时，CD任务栈到顶部，显示D（此时没有触发onNewIntent事件）

### A=singleInstance, C=singleTask, D=standard
[A]->[C->D]->[A]->C 第二次从A启动C时，D被销毁，C到栈顶

### ABCD=standard, 启动时添加flag=FLAG_ACTIVITY_NEW_TASK
[A->B->C]->[D]->B1 从C启动D时，因为taskAffiinity不同，所以D会创建新的任务栈；从D启动B时，会在原来的任务栈中创建新的实例，变为[A->B->C->B1]

### ABCD=standard, 从ABC启动时添加flag=FLAG_ACTIVITY_NEW_TASK，从D启动时没有FLAG_ACTIVITY_NEW_TASK
[A->B->C]->[D->B1]->[C1]->D 从C启动D时，因为taskAffiinity不同，所以D会创建新的任务栈；从D启动B时，因为没有FLAG_ACTIVITY_NEW_TASK且B是standard，所以创建新的实例B1，并和D在一个任务栈中；从B1启动C时，因为有FLAG_ACTIVITY_NEW_TASK且C对应的taskAffiinity的任务栈已存在，所以会在原来的任务栈中创建新的C1实例；从C1启动D时，有FLAG_ACTIVITY_NEW_TASK且D的任务栈已存在，则将DB任务栈移到顶部，显示B

## 总结：
* launchMode=singleInstance时系统中只有一个实例，由此启动的Activity都会创建新的任务栈。
* launchMode=singleTask时系统中只有一个实例，该特性决定了在启动该Activity时，在其顶部的Activity会被销毁。
任务栈的情况配合taskAffinity属性有不同的结果(taskAffinity属性值默认是包名)。
如果启动它的Activity与其具有相同的taskAffinity属性，则在同一个任务栈中，否则会创建新的任务栈。
