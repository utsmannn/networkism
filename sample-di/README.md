### Manual Dependencies Injection

You need create container class when include of `Flow<NetworkismResult>` and inject this class, you can use Koin or Dagger, or another DI library.

#### Sample Container
```kotlin
class NetworkismContainer(application: Application) {

    // public flow variable
    val networkism = Networkism.flow(application)
}
```

The Networkism has function `Networkism.initLifecycle` for dependencies injection compatibility.
#### `Networkism.initLifecycle` param table
| param | desc |
| --- | --- |
| `Flow<NetworkismResult>` | the flow of `NetworkismResult`, retrieved from public container of Networkism |
|  `CoroutineScope` | required for lifecycle aware of activity/fragment |
| `NetworkismListener` | the listener |