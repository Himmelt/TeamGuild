# TeamGuild
TeamGuild

### 指令
```
/guild|team                        主命令( 也可以使用/team )
/guild top                         查看公会排行榜
/guild info [player]               查看自己公会或<player>公会的基本信息
/guild disband [leader]            会长解散公会,或由OP解散会长<leader>的公会,需要管理权限
/guild create [display]            创建公会,可选参数设置公会显示名.
/guild join <leader>               向<leader>的公会发送加入申请
/guild list                        列出所在公会的成员
/guild leave                       退出当前公会
/guild kick <player>               将<player>移出公会（会长副会长可执行）
/guild accept <player>             接受<player>的加入申请（会长副会长可执行）
/guild reject <player>             拒绝<player>的加入申请（会长副会长可执行）
/guild display [name]              查看/设置公会显示名（仅会长可执行）
/guild describe [description]      查看/设置公会简介（仅会长可执行）
/guild upgrade                     升级公会（仅会长可执行）
/guild attorn ...                  转让公会
/guild invite ...                  邀请成员
/guild setman <member>             将成员<member>设为公会管理员
/guild unsetman <manager>          移除公会管理员<manager>的管理权限变为普通成员
```

### 配置
```hocon
# 显示语言
lang = zh_cn
# 调试模式
debug = true
# 需要对接的经济系统插件
# 目前支持的经济插件：
# 1. Vault (任何实现了Vault经济系统的插件都可以，例如PlayerPoints)
# 2. Essentials
# 3. PlayerPoints
ecoType = Vault
# 是否允许公会成员PVP（重启生效）
teamPvP = false
# 公会显示名最大长度，每个中文占2个长度，颜色代码也算在长度内
maxDisplay = 10
# 公会简介最大长度，每个中文占2个长度
maxDescription = 100
# 公会等级设置
# 等级列表，插件会从低到高自动排序，不必手动排序
levels = [
  {
    size = 5       # 该等级的公会最大成员数，是区分等级的唯一标志，不可重复
    cost = 10      # 创建公会或从上一级升级到本级所需费用
    mans = 1       # 该等级的公会的管理员数量(不得超过最大成员数量，size)
    guild = false  # 是否是公会(暂时没啥效果)
  }
  {
    size = 5
    cost = 10
    mans = 1
    guild = false
  }
]
```

### 公会配置
```hocon
# 会长名，是公会的唯一识别标志
Himmelt {
  # 公会显示名 
  display = "&5紫罗兰&r"
  # 公会等级，即最大成员数，插件会自动匹配到相应的等级
  size = 15
  # 公会资金(未实现)
  balance = 0
  # 公会简介
  description = Himmelt's Team.
  # 公会管理员
  managers = [
    Shiki
    Rikka
  ]
  # 公会成员
  members = [
    Kasei
    Miral
  ]
  # 待处理加入申请
  applications = [
    Bob
    Shina  
  ]
}
```
