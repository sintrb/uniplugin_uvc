<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		},
		onError: function(err) {
			// 这里只能捕获方法内的异常，不能捕获生命周期中的逻辑异常  
			let json = JSON.stringify(err);
			let arr = json.split("\\n");
			let log = {
				message: arr[1],
				stack: err
			}
			this.$throw(log)
		},
	}
</script>

<style>
	/*每个页面公共css */
	@import './common/uni-nvue.css';
</style>
