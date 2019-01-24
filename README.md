# TeamGuild
TeamGuild

### 指令
```
/teamguild|team                   主命令
/team top                         查看公会排行榜
/team info [player]               查看自己公会或<player>公会的基本信息
/team disband [leader]            会长解散公会,或由OP解散会长<leader>的公会,需要管理权限
/team create [display]            创建公会,可选参数设置公会显示名.
/team join <leader>               向<leader>的公会发送加入申请
/team accept join <player>        接受<player>的加入申请（会长副会长可执行）
/team reject join <player>        拒绝<player>的加入申请（会长副会长可执行）
/team attorn <player>             转让公会给 <player>
/team unattorn                    取消转让公会
/team accept attorn <leader>      接受 <leader> 公会的转让
/team reject attorn <leader>      拒绝 <leader> 公会的转让
/team invite <player>             邀请 <player> 加入公会
/team uninvite <@ALL|player>      取消邀请 <player> 如果参数为 @ALL 则取消所有邀请
/team accept invite <leader>      接受 <leader> 公会的邀请
/team reject invite <leader>      拒绝 <leader> 公会的邀请
/team setman <member>             将成员<member>设为公会管理员
/team unsetman <manager>          移除公会管理员<manager>的管理权限变为普通成员
/team fame give <leader> <fame>   给与 <leader> 公会 <fame> 点 荣耀, 需要管理权限
/team fame take <leader> <fame>   移除 <leader> 公会 <fame> 点 荣耀, 需要管理权限
/team fame give <member> <fame>   给与 <member> 成员 <fame> 点 荣耀, 需要管理权限
/team fame take <member> <fame>   移除 <member> 成员 <fame> 点 荣耀, 需要管理权限
/team topjoin <true|false>        设置是否在排行榜显示 "点击加入" 的按钮，仅会长可设置
/team home                        (未实现) 前往公会领地传送点
/team sethome                     (未实现) 设置公会领地传送点(仅会长)
/team list                        列出所在公会的成员
/team leave                       退出当前公会
/team kick <player>               将<player>移出公会（会长副会长可执行, 会长可以踢除自己外的所有人,副会长只能踢普通成员）
/team display [name]              查看/设置公会显示名（仅会长可执行）
/team describe [description]      查看/设置公会简介, 可使用 \n 换行（仅会长可执行）
/team upgrade                     升级公会 (仅会长可执行)
/team topkit                      领取排行榜每日奖励(仅会长可执行)
```

### 配置
```hocon
# 插件版本，用于自动任务，请勿修改 !!
version = 1.1.3
# 显示语言
lang = zh_cn
# 调试模式
debug = true
# 自动重新释放语言文件(版本变化时).
autoUpLang = true
# 需要对接的经济系统插件
# 目前支持的经济插件: 
# 1. Vault经济服务 (任何实现了Vault经济服务的插件都可以, 例如PlayerPoints)
# 2. Essentials
# 3. PlayerPoints
ecoType = Essentials
# 没有经济系统时是否允许创建公会.
ignoreNoEco = true
# 是否允许公会内PVP (重启生效).
teamPvP = false
# 转让公会后是否直接脱离公会.
attornLeave = false
# 公会显示名最大长度, 每个中文占2个长度, 颜色代码也算在长度内.
maxDisplay = 17
# 公会简介最大长度, 每个中文占2个长度.
maxDescription = 100
# 用于聊天文字点击的命令, 此处已默认插件主命令,
# 请勿随意修改, 只有在默认命令冲突无效时才需要修改.
textCommand = /team
# 公会等级设置
# 等级列表, 插件会从低到高自动排序, 不必手动排序.
levels {
  0 {
    # 该等级公会成员容量
    size = 5
    # 该等级公会升级费用
    cost = 10
    # 该等级公会管理数量
    mans = 1
  }
  1 {
    # 该等级公会成员容量
    size = 10
    # 该等级公会升级费用
    cost = 20
    # 该等级公会管理数量
    mans = 3
  }
  2 {
    # 该等级公会成员容量
    size = 15
    # 该等级公会升级费用
    cost = 30
    # 该等级公会管理数量
    mans = 5
  }
}
```

### 公会配置
```hocon
# 会长名，是公会的唯一识别标志
Himmelt {
  # 公会等级，和配置中的 levels 对应
  level = 0
  # 公会荣耀值
  fame = 30
  # 公会资金(未实现)
  balance = 0
  # 公会显示名 
  display = "&5紫罗兰&r"
  # 公会简介
  description = Himmelt's Team.
  # 公会成员
  members = [
    Kasei
    Miral
  ]
  # 公会管理员
  managers = [
    Shiki
    Rikka
  ]
  # 待处理加入申请
  applications = [
    Bob
    Shina  
  ]
}
```
