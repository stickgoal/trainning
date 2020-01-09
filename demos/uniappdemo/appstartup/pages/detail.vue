<template>
	<view>
		<!-- 自定义底部按钮 -->
		<uni-card :title="detail.title"  :is-shadow="true"  :note="'作者 : '+detail.author +' 发布时间：'+detail.publishTime"
>
		    {{detail.content}}
			
			

		</uni-card>
	</view>
</template>

<script>
	import uniCard from "@/components/uni-card/uni-card"

	export default {
		components: {
			uniCard
		},
		data() {
			return {
				detail: {}
			}
		},
		onLoad(option) {
			console.log("加载完成，参数" + option);
			uni.request({
				url: "http://192.168.20.121:8081/post/detail?id=" + option.id,
				success: (response) => {
					if (response.data.success) {
						this.detail = response.data.data;
					} else {
						uni.showToast("请求出错，请稍候再试")
					}
				}
			})
		},
		methods: {

		}
	}
</script>

<style>

</style>
