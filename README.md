# KalznJudgerServer

## 了解本JudgerServer
### 基本情况
Kalzn judger server是用于Online Judge的评测服务器，用于接受Online Judge的评测任务请求。其中任务队列及评测数据处理引擎基于Spring（MVC），整合为SpringBoot。评测核心基于ASNI C。在此进入<a href = "https://github.com/Kalzncc/KalznCodeJudger">Judger Core仓库</a>以了解评测核心。在本服务器运行时，要配置指定另一个Leader Server，本服务器以p2p的方式与之通信。Leader Server用于向Judger Server发布评测任务，并提供评测数据的支持，Judger Server每次只能与一个Leader Server建立从属关系，Leader Server可以管理数个Judger Server。
### Http接口列表
Judger Server将向Leader Server提供以下接口
|接口url|参数|作用|
|-|-|-|
|/Config/SetLeader（get）|token（用于验证）、server-id（分配給本server的id）、scheme（http或https）、ip（Leader Server的ip地址）、port（Leader Server的端口号）|用于设定Judge Server的隶属的Leader Server，token是验证方式，由Judger Server在初始化是自动生成，在快速开始章节了解更多|
|/Task/Submit（post）|config-str（配置本次评测任务的json串，之后会进一步解释）、code（待评测代码）|向judger server提交一个评测任务，Judger Server会在任务评测完成后通知到Leader Server的特定接口|
|/Data/Update（get）|data-id（要更新的数据ID，即为问题ID）|让Judger Server从Leader Server拉取最新的评测数据，Judger Server接受到此指令后，将从Leader Server的特定接口拉取dataID的最新版评测数据到本地。另外，你无需时刻调用这个接口以保证评测数据是最新版的，Judger Server在每一个任务开始运行前，都会向Leader Server发送评测数据版本的核验请求，发现新版数据后会自动拉取。此接口只是用于提前让Judger Server拉取一些题目以提高比赛的评测速度。|
|/Data/ClearDataSpace（get）|无|清扫数据区，Judger Server接到此指令后，如果数据区占用空间大于服务器的设定值，就启动清理，Judger Server将优先选择最久未使用的评测数据进行清理，直到数据区占用空间小于设定值。|
|/Data/ResetData(get)|无|重置数据区，Judger Server只会在暂停服务时响应这个请求。接到这个请求后，Judger Server将**在不做任何线程安全保证**的情况下释放数据区所有数据，并注销所有数据，此外，在服务器更换从属Leader Server后，将执行这个重置操作。此接口应谨慎使用。|
|/Data/ReleaseData(get)|data-id(要释放的数据ID号)|Judger Server在接到这个请求后，将dataID的数据从数据区释放，但并不会注销此数据。|
|/Data/CheckManagerStatus(get)|无|查看当前DataManager的服务状态，（准备中、暂停中、运行中、已停机）,值得注意的是，DataManager无论在什么状态下，都可以接受以上所有接口的请求，这里的服务是指，DataManger会在服务器设定的间隔时间内，扫描数据区的大小，数据区占用空间大于服务器的设定值，就启动清理。服务状态是指这个服务的启动与否。|
|/Data/PauseService(get)|无|暂停DataManager的服务，这里的服务亦指上述服务|
|/Data/StartService(get)|无|开始DataManager的服务，这里的服务亦指上述服务|
|/Setting/Hello(get)|无|用于测试通信的接口，返回Judger Server的所用配置|
|/Setting/RegisterData(get)|data-id(要注册的数据ID号)|在一个题目数据可以评测前，你需要先在Judger Server上注册这个数据，比如现在OJ上挂了一个新题目，它的ID号是1004，此时你必须调用这个接口以注册1004号数据，然后才能在此Judger Server上评测这个题目，注册题目不意味这它会被立刻拉取到Judger Server的数据区。只是让Judger Server知道有这个题目的存在。|
|/Setting/PauseService(get)|无|暂停Judger Server的服务，与暂停DataManager不同，它将同时暂停TaskManager以及DataManager的服务，这就意味这服务器彻底暂停提供服务，这里值得主要的是，暂停服务是软暂停，它只是不在接受新的评测任务，还在Task Manager队列中的未完成的评测任务不会作废，而是会在完成后正常发送给Leader Server的特定接口。|
|/Setting/StartService(get)|无|开启Judger Server的服务|
### Leader Server的接口列表
Leader Server需要向Judger Server提供以下接口
|接口url|参数|返回Json项(文件)|作用|
|-|-|-|-|
|/DataCheck(get)|serverID(本Judger Server的ID号)、dataID(要核验的数据ID)|serverID、dataID、thisServerVersion、LeaderServerVersion|Judger Server用此接口核验数据版本是否一致，thisServerVersion是本Judger Server的数据区的版本号，如果不存在即为-1，LeaderServerVersion是Leader Server数据库中最新数据版本。当两者不一致时，Judger Server将从Leader Server特定接口拉取最新数据。|
|/DataDownLoad(get)|dataID(要下载的数据ID)|data.zip|Leader Server返回该数据ID的最新数据，压缩包内容物之后会进一步解释。|
|/ReportTask(get)|taskID(任务ID号)|无要求|Judger Server通过此接口通知Leader Server某任务已经开始运行，以供Leader Server了解任务评测进度。|
|/AllEnableDataID(get)|无|所有需要注册的dataID的集合，例如[1001,1002,1004]|用于Judger Server在配置从属的Leader Server完成后快速注册所有可用dataID。|
|/ReportResult(get)|result(评测结果json，之后会进一步解释)|无要求|Judger Server向Leader Server提交评测结果。|

### 参数介绍和举例
对上述的几个参数进行介绍
#### config-str 任务请求配置Json
|包含项|说明|
|-|-|
|taskID|任务的id号，尽量保证唯一，至少保证不会有两个taskID一样的任务同时在Judger Server上评测。|
|dataID|任务的评测数据的ID号。|
|compilerID|任务所使用的编译器ID号|
|sPJ|bool型，是否启用spj|
|judgeMode|核心的评测模式，详情请见<a href = "https://github.com/Kalzncc/KalznCodeJudger">Judger Core仓库</a>的解释|
|strictMode|核心严格模式，详情请见<a href = "https://github.com/Kalzncc/KalznCodeJudger">Judger Core仓库</a>的解释|
|iOMode|核心IO模式，详情请见<a href = "https://github.com/Kalzncc/KalznCodeJudger">Judger Core仓库</a>的解释|
下面是一个例子：
```json
{
    "taskID":"10004",
    "dataID":"1001",
    "compilerID":"1",
    "sPJ":false,
    "judgeMode":"0",
    "strictMode":1,
    "iOMode":"0"
}
```
#### result 任务结果配置Json
由于Json置太长，这里直接给出一个json示例：
```json
{
  "realTimeID": "15", //评测实时ID，即为taskID
  "judgerConfig": { // 核心评测请求配置，详情请见Judger Core仓库
    "taskID": "15",
    "maxCharBuffer": 100000000,
    "judgeMode": 0,
    "iOMode": 0,
    "strictMode": 0,
    "workSpacePath": "WorkSpace/15",
    "resultPath": "result.json",
    "logPath": "CoreSpace/core_log.log",
    "sPJ": false,
    "translator": {
      "mode": 0,
      "translatorId": 1,
      "codeFileName": "main.cpp",
      "compilerPath": "/usr/bin/g++",
      "compilerInfoPath": "compiler.log",
      "compilerOptions": [
        "g++",
        "main.cpp",
        "-o",
        "main"
      ],
      "compilerProductName": "main"
    },
    "data": [
      {
        "inputData": "data/1.in",
        "outputData": "data/output.out",
        "stdAnswer": "data/1.out",
        "maxCPUTime": 1000,
        "maxMemory": 102400,
        "maxStack": 1024000
      },
      {
        "inputData": "data/2.in",
        "outputData": "data/output.out",
        "stdAnswer": "data/2.out",
        "maxCPUTime": 1000,
        "maxMemory": 102400,
        "maxStack": 1024000
      }
    ],
    "dataID": 1,
    "CodeSourcePath": "CodeSpace/15/main.cpp",
    "CodeFileName": "main.cpp"
  },
  "resultConfig": { // 核心结果信息，详情请见Judger Core仓库
    "taskID": "15",
    "doneTime": "2021-12-02 18:11:19",
    "judgeTime": 431,
    "extraTime": 431,
    "result": [
      {
        "time": 0,
        "realTime": 0,
        "memory": 0,
        "signal": 0,
        "code": 0,
        "result": 7,
        "detail": "Code compile failed"
      },
      {
        "time": 0,
        "realTime": 0,
        "memory": 0,
        "signal": 0,
        "code": 0,
        "result": 8,
        "detail": "No info"
      }
    ]
  },
  "logString": "No info.", // log内容
  "compilerInfoString": "main.cpp:16:1: error: ?aor: ?IO? is not a nae\n   24 | using namespace IO;\n",
    // 编译器输出

  "interpreterInfoString": "",
    // 解释器输出
  "TaskInfo": "No info.",
    // 任务信息，如果出现系统错误，会再次记录
  "ExceptionCode": 0
    // 异常代码，0为无异常，之后进一步解释
}
```
### data.zip 评测数据包
在评测数据包中，处理数据外，最重要的是index.json文件，此文件是数据文件清单，用于Judger Server解析数据，文件中记录了数据文件清单，及每个文件的资源限制信息。index.json应为每个编程语言准备一份。所以对于每个compilerID，data.zip中都存在文件compilerID/index.json，以表示该语言的数据清单。如果这个题目需要spj，则编译好的spj程序需要被命名为spj，置于压缩包根目录。以下是一个标准的含有spj的评测数据包目录：
其中index.json的内容举例，具体含有请见Judger Core库的介绍：
```json
[
  {
    "inputData": "data/1.in",
    "outputData": "data/output.out",
    "stdAnswer": "data/1.out",
    "maxCPUTime": 1000,
    "maxMemory": 102400,
    "maxStack": 1024000
  },
  {
    "inputData": "data/2.in",
    "outputData": "data/output.out",
    "stdAnswer": "data/2.out",
    "maxCPUTime": 1000,
    "maxMemory": 102400,
    "maxStack": 1024000
  }
]
```
### 使用token配置Leader Server
当Judger Server启动后，会自动生成一个token，你可以也只能使用这个token改变JudgerServer从属的LeaderServer，你也可以在Judger Server的日志文件中找到这个token。尽管如此，仍不建议将Leader Server和Judger Server由公开网络建立链接，尽量使私有网络链接。
![DR46`7O7)CFE9A P~_}JGYS](https://user-images.githubusercontent.com/44296812/144430364-8ce43a52-e4aa-40d8-b39a-b0fbabc0ef91.png)

### 服务器配置yaml
```yaml
server:
  port: 8082
logging:
  file:
    name: /home/kalzn/project/git/JudgerServer/space/LogSpace/server_log.log
  level:
    root: warn
    com.kalzn.judger.runner: info
#springboot的相关配置

judger:
  max-json-file-size: 100000000
  #核心读取json配置的最大大小
  judger-core-path: /home/kalzn/project/git/JudgerServer/space/CoreSpace/judger
  #judger core的目录
  judger-core-log-path: /home/kalzn/project/git/JudgerServer/space/LogSpace/core_log.log
  #judger core的日志目录
  judger-work-space: /home/kalzn/project/git/JudgerServer/space/WorkSpace
  #Judger Server的工作目录，TaskManager将任务评测产生的文件放置于此，评测完成即删除
  judger-data-space-path: /home/kalzn/project/git/JudgerServer/space/DataSpace
  #Judger Server的数据区目录，从Leader Server拉取的评测数据将放置于此，由DataManager定期清理
  judger-code-space-path: /home/kalzn/project/git/JudgerServer/space/CodeSpace
  #任务的待测代码存放的路径，评测完成即删除，由CodeManager管理
  config-token-length: 32
  #Judger Server初始化生成的token的长度
  leader-server-timeout: 5000
  #Judger Server请求Leader Server的超时时限
  data-download-buffer-size: 1024
  #评测数据下载的缓存区大小
  max-spj-time: 30000
  #SPJ程序的最大时限 ms
  max-spj-memory: 1024000
  #SPJ程序的最大内存限制 KB
  max-buffer-char: 100000000
  #Judger Core的输出缓存区大小 （详情请见Judger Core仓库）
  max-task-running-size: 10
  #Runbox线程池最大数量（同时运行待测程序的最大数目，建议根据机器性能而定）
  max-task-waiting-size: 100
  #等待队列最大数量，Leader Server提交任务请求时，如果队列已满，则阻塞等待。
  min-active-task-thread-pool-number: 5
  #Runbox线程池保持活跃的线程数
  task-thread-active-time: 500
  #Runbox线程池保持活跃的时间 ms
  data-space-max-size: 300
  #数据区最大占用空间（当DataManager开始清理数据区时，如果数据区占用空间大于这个，就开始清除数据。） MB
  data-space-clear-size: 128
  #数据区的清理大小（当DataManager开始清理数据区，一直清除直至数据区占用空间小于这个数停止）MB
  data-space-size-check-time: 30
  #DataManager扫描数据区的时间间隔  minute



  #以下是编译器配置（原谅我的不标准表达，有时我说编译器，其实是指翻译器，包括编译器和解释器），是一个Map集合，键值是compilerID，value是其具体配置，配置项意义请见Judger Core仓库的解释。
  translators:
    1:
      translator-id: 1
      mode: 0
      code-file-name: main.cpp
      compiler-path: /usr/bin/g++
      compiler-info-path: compiler.log
      compiler-options: [g++, main.cpp, -o, main]
      compiler-product-name: main
    2:
      translator-id: 2
      mode: 2
      code-file-name: Main.java
      compiler-path: /usr/bin/javac
      compiler-info-path: compiler.log
      compiler-options: [javac, Main.java]
      compiler-product-name: Main.class
      interpreter-path: /usr/bin/java
      interpreter-info-path: interpreter.log
      interpreter-options: [java, Main]
    3:
      translator-id: 3
      mode: 1
      code-file-name: main.py
      interpreter-path: /usr/bin/python
      interpreter-info-path: interpreter.log
      interpreter-options: [python, main.py]















```
### 报错和排除

在result的参数配置中ExceptionCode指示了报错的类型，TaskInfo指示了错误的具体信息
|代码|说明|
|-|-|
|0|评测成功|
|1|Judger Core错误，这是评测核心而不是任务管理引擎的报错，此时TaskInfo不会提示信息（任务管理引擎无法获取核心报错）此时result结果应为System error，具体错误会在评测结果中展现，如果评测结果指示访问日志文件以了解更多，则可到judge core的log文件中寻找报错原因。具体前往Judger Core仓库了解详情|
|2|任务管理引擎报错，在TaskInfo中可以找到报错信息。包括无法建立评测环境（无法在work space中建立文件、无法请求评测数据到work space）；无法报告任务（无法通知Leader Server任务已经开始）；无法启动Judger Core；无法收集评测结果。|
