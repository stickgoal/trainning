from fastapi import FastAPI

app = FastAPI()

@app.get("/")
async def root():
    return {"message":"Hello World!"}


@app.get("/user/{user_id}")
async def get_user_by_id(user_id):
    return {'username':'zhangsan','user_id':user_id}
