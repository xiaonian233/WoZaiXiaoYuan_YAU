# -*- encoding:utf-8 -*-
import requests
import json
from urllib.parse import urlencode
import time

pushtoken = ''  # 这里填pushplus的token，可以在http://www.pushplus.plus/ 扫码注册
user = {
    'username': '',  # 这里填账号(手机号)
    'password': ''  # 这里填密码
}
loginId = ''
sid = ''

try:
    loginUrl = "https://gw.wozaixiaoyuan.com/basicinfo/mobile/login/username"
    header = {
        "Accept-Encoding": "gzip, deflate, br",
        "Connection": "keep-alive",
        "User-Agent": "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36 MicroMessenger/7.0.9.501 NetType/WIFI MiniProgramEnv/Windows WindowsWechat",
        "Content-Type": "application/json;charset=UTF-8",
        "Content-Length": "2",
        "Host": "gw.wozaixiaoyuan.com",
        "Accept-Language": "en-us,en",
        "Accept": "application/json, text/plain, */*"
    }
    user = {
        'username': '',  # 这里填账号(手机号)
        'password': ''  # 这里填密码
    }
    # pushtoken = ''  # 这里填pushplus的token，可以在http://www.pushplus.plus/ 扫码注册

    body = "{}"


    def setJwsession(jwsession):
        header['JWSESSION'] = jwsession

    def main(username, password):
        url = loginUrl + "?username=" + str(user['username']) + "&password=" + str(user['password'])
        session = requests.session()
        response = session.post(url=url, data=body, headers=header)
        res = json.loads(response.text)
        if res["code"] != 0:
            print("登陆失败，请检查账号信息")
            requests.get(
                'http://www.pushplus.plus/send?token={}&title=我在校园定位签到&content=登陆失败,请检查账号信息&template=html'.format(
                    pushtoken))
            time.sleep(5)
        else:
            print("登陆成功")
            jwsession = response.headers['JWSESSION']
            setJwsession(jwsession)

            header['Host'] = "student.wozaixiaoyuan.com"
            header['Content-Type'] = "application/x-www-form-urlencoded"
            url = "https://student.wozaixiaoyuan.com/sign/getSignMessage.json"
            sign_data = {
            "page": "1",
            "size":"1",
            }
            data = urlencode(sign_data)
            response = session.post(url=url, data=data, headers=header)
            response = json.loads(response.text)
            logId = response["data"][0]["logId"]
            sid = response["data"][0]["id"]
            header['Content-Type'] = "application/json"
            url = "https://student.wozaixiaoyuan.com/sign/doSign.json"
            data = {
                "id": logId,
                "signId":sid,
                "latitude":"36.59141",
                "longitude":"109.49303",
                "country":"中国",
                "province":"陕西省",
                "city":"延安市",
                "district":"宝塔区",
                "township":"南市街道",
                }
            response = session.post(url=url, json=data, headers=header)
            response = json.loads(response.text)
            print(response["code"])
            if response["code"] == 0:
                requests.get(
                    'http://www.pushplus.plus/send?token={}&title=我在校园定位签到&content=打卡成功&template=html'.format(pushtoken))
            else:
                requests.get(
                    'http://www.pushplus.plus/send?token={}&title=我在校园定位签到&content=打卡失败&template=html'.format(pushtoken))


    if __name__ == '__main__':
        main(user['username'], user['password'])

except:
    requests.get('http://www.pushplus.plus/send?token={}&title=我在校园定位签到&content=打卡失败&template=html'.format(pushtoken))
