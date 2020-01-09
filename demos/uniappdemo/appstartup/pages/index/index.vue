<template>
	<view>
		
		<uni-list >
		    <uni-list-item v-for="(p,i) in posts" :key="i" :title="p.title" :show-arrow="true" :note="p.content" @click="showDetail(p.postId)">
				
			</uni-list-item>
		   
		</uni-list>
	</view>
</template>

<script>
import uniCard from "@/components/uni-card/uni-card"
import uniList from "@/components/uni-list/uni-list.vue"
import uniListItem from "@/components/uni-list-item/uni-list-item.vue"
export default {
	components:{
		uniCard,uniList,uniListItem
	},
		data() {
			return {
				posts:[{postId:1,title:'帖子1',content:'我的第一篇帖子，大家轻拍'},{postId:2,title:'贴2',content:'我的第篇帖子，大家轻拍'},{postId:3,title:'3',content:'easdfasdfsdfsad阿斯顿发大水'}]
			}
		},
		onLoad() {//相当于vue的mounted生命周期钩子
			//发送请求，更新数据
			console.log("onload ")
			
			uni.request({
			    url: 'http://192.168.20.121:8081/post/getAll', 
			   /* data: {
			        text: 'uni.request'
			    },
			    header: {
			        'custom-header': 'hello' //自定义请求头信息
			    }, */
			    success: (res) => {
			        console.log(res.data);
					if(res.data.success){
						this.posts=res.data.data;
					}else{
						console.error(res.message)
						uni.showToast("请求数据失败，请稍候再试....")
					}
			    }
			});
			
		}
		,
		methods: {
			showDetail:function(id){
				console.log(id);
				uni.navigateTo({
					url:'/pages/detail?id='+id
				})
			}
			
		}
	}
</script>

<style>

</style>
