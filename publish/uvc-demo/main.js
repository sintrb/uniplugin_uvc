import Vue from 'vue'
import App from './App'

Vue.config.productionTip = false


//系统错误捕获  
const _errorHandler = (err, vm, info) => {
	console.error(err);
	uni.showModal({
		title: '错误',
		content: err.message + "\n\n---\n\n" + err.stack,
		showCancel: false,
		confirmText: "确定",
	});
}
Vue.config.errorHandler = _errorHandler;
Vue.prototype.$throw = (error) => _errorHandler(error, this);

App.mpType = 'app'

const app = new Vue({
	...App
})
app.$mount()
