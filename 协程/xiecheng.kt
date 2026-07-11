Class MainActivity:AppCompatActivity() {
	//创建一个固定大小的线程池
	private val threadPool = Executors.newFixedThreadPool(4)
	//将线程池转换为 CoroutineDispatcher
	private val customDispatcher = threadPool.asCoroutineDispatcher()
	//创建一个自定义作用域 CoroutineScope，使用自定义线程池作为调度器
	private val coroutineScope = CoroutineScope(customDispatcher)
	//创建父协程 Job
	private lateinit var parentJob:Job
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		//启动一个父协程
		parentJob = coroutineScope.launch {
			println("父协程开始执行：${Thread.currentThread().name}")
			//启动多个子协程
			val job1 = launch {simulateTask(1)}
			val job2 = launch {simulateTask(2)}
			val job3 = launch {simulateTask(3)}
			val job4 = launch {simulateTask(4)}
			
			//立刻取消 job1
			job1.cancel()
			
			//等待所有子协程完成
			joinAll(job1,job2,job3,job4)
			println("父协程完成")
		}
	}
	
	override fun onDestroy() {
		super.onDestroy()
		//取消父协程及其所有子协程
		if(::parentJob.isInitialized && parentJob.isActive) {
			parentJob.cancel()
			println("在 onDestroy 中取消父协程及其子协程")
		}
		threadPool.shutdown()
	}
	
	//模拟一个耗时任务
	private suspend fun simulateTask(taskId:Int) {
		try{
			delay(1000L)
			println("任务 ￥taskId 完成，线程：${Thread.currentThread().name}")
		}catch(e:CancelllationException) {
			println("任务 $taskId 被取消")
		}
	}
}