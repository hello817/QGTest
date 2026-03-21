package com.dorm;

import com.dorm.entity.*;
import com.dorm.mapper.*;
import com.dorm.utils.MyBatisUtil;
import org.apache.ibatis.session.SqlSession;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
//加密选用md5，不加盐
public class Main {

    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);
    //账号格式
    private static final Pattern STUDENT_PATTERN = Pattern.compile("^(3125|3225)\\d{6}$");
    private static final Pattern ADMIN_PATTERN = Pattern.compile("^0025\\d{6}$");

    // 加密工具
    private static class EncryptUtil {
        public static String encrypt(String input) {
            try {
                java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
                byte[] hash = md.digest(input.getBytes("UTF-8"));
                StringBuilder hex = new StringBuilder();
                for (byte b : hash) {
                    hex.append(String.format("%02x", b));
                }
                return hex.toString();
            } catch (Exception e) {
                return input;//加密失败就返回原输入
            }
        }
        public static boolean check(String input, String encrypted) {
            return encrypt(input).equals(encrypted);
        }
    }

    public static void main(String[] args) {
        System.out.println("================================");
        System.out.println("      宿舍报修管理系统");
        System.out.println("================================");

        while (true) {
            try {
                if (currentUser == null) showLoginMenu();
                else if (currentUser.isStudent()) showStudentMenu();
                else if (currentUser.isAdmin()) showAdminMenu();
            } catch (Exception e) {
                System.out.println("错误：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ==================== 登录相关 ====================
    //菜单
    private static void showLoginMenu() {
        System.out.println("\n【主菜单】");
        System.out.println("1. 登录");
        System.out.println("2. 注册");
        System.out.println("3. 退出");
        System.out.print("请选择：");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1": login(); break;
            case "2": register(); break;
            case "3": System.out.println("再见！"); System.exit(0); break;
            default: System.out.println("输入错误");
        }
    }
    //登录模块
    private static void login() {
        System.out.println("\n【用户登录】");
        System.out.print("账号：");
        String account = scanner.nextLine();
        System.out.print("密码：");
        String password = scanner.nextLine();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectByAccount(account);
            if (user == null) {
                System.out.println("❌ 账号不存在");
                return;
            }
            if (!EncryptUtil.check(password, user.getPassword())) {
                System.out.println("❌ 密码错误");
                return;
            }
            currentUser = user;
            System.out.println("✅ 登录成功！欢迎 " + account);
        } catch (Exception e) {
            System.out.println("❌ 登录失败：" + e.getMessage());
        }
    }
    //注册模块
    private static void register() {
        System.out.println("\n【用户注册】");
        System.out.print("请选择角色（1-学生，2-管理员）：");
        String roleChoice = scanner.nextLine();

        String role;
        Pattern pattern;
        String typeName;
        if ("1".equals(roleChoice)) {
            role = "student";
            pattern = STUDENT_PATTERN;
            typeName = "学号";
        } else if ("2".equals(roleChoice)) {
            role = "admin";
            pattern = ADMIN_PATTERN;
            typeName = "工号";
        } else {
            System.out.println("❌ 角色选择错误");
            return;
        }

        System.out.print("请输入" + typeName + "（10位）：");
        String account = scanner.nextLine();
        System.out.print("请输入密码（至少6位）：");
        String password = scanner.nextLine();
        System.out.print("请确认密码：");
        String confirm = scanner.nextLine();

        if (account == null || account.trim().isEmpty()) {
            System.out.println("❌ " + typeName + "不能为空");
            return;
        }
        if (password.length() < 6) {
            System.out.println("❌ 密码长度至少6位");
            return;
        }
        if (!password.equals(confirm)) {
            System.out.println("❌ 两次密码不一致");
            return;
        }
        if (!pattern.matcher(account).matches()) {
            System.out.println("❌ " + typeName + "格式错误，必须为10位且前缀正确");
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            if (mapper.selectByAccount(account) != null) {
                System.out.println("❌ 账号已存在");
                return;
            }
            User user = new User(account, EncryptUtil.encrypt(password), role);
            mapper.insert(user);
            System.out.println("✅ 注册成功！请登录");
        } catch (Exception e) {
            System.out.println("❌ 注册失败：" + e.getMessage());
        }
    }

    // ==================== 学生菜单 ====================
    //菜单
    private static void showStudentMenu() {
        System.out.println("\n【学生菜单】");
        System.out.println("当前用户：" + currentUser.getAccount());
        if (currentUser.hasDorm()) {
            System.out.println("宿舍：" + currentUser.getBuilding() + currentUser.getRoomNumber());
        } else {
            System.out.println("⚠️ 还未绑定宿舍");
        }

        System.out.println("\n1. 绑定宿舍");
        System.out.println("2. 创建报修单");
        System.out.println("3. 查看我的报修记录");
        System.out.println("4. 取消报修单");
        System.out.println("5. 查看我的基本信息");
        System.out.println("6. 修改密码");
        System.out.println("7. 退出登录");
        System.out.print("请选择：");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1": bindDorm(); break;
            case "2": createRepairOrder(); break;
            case "3": viewMyOrders(); break;
            case "4": cancelOrder(); break;
            case "5": viewMyInfo(); break;
            case "6": changePassword(); break;
            case "7": currentUser = null; System.out.println("已退出登录"); break;
            default: System.out.println("输入错误");
        }
    }
    //显示在菜单中，现实目前的信息，方便确认以及调试
    private static void viewMyInfo() {
        System.out.println("\n【我的基本信息】");
        System.out.println("账号：" + currentUser.getAccount());
        System.out.println("角色：" + (currentUser.isStudent() ? "学生" : "管理员"));
        System.out.println("宿舍：" + (currentUser.hasDorm() ? currentUser.getBuilding() + currentUser.getRoomNumber() : "未绑定"));
        System.out.println("注册时间：" + currentUser.getCreateTime());
    }
    //绑定宿舍
    private static void bindDorm() {
        System.out.println("\n【绑定宿舍】");
        System.out.print("楼栋（如：A栋）：");
        String building = scanner.nextLine();
        System.out.print("房间号（如：101）：");
        String roomNumber = scanner.nextLine();

        if (building.isEmpty() || roomNumber.isEmpty()) {
            System.out.println("❌ 楼栋和房间号不能为空");
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            currentUser.setBuilding(building);
            currentUser.setRoomNumber(roomNumber);
            mapper.update(currentUser);
            System.out.println("✅ 绑定成功！");
        } catch (Exception e) {
            System.out.println("❌ 绑定失败：" + e.getMessage());
        }
    }
    //创建保修单
    private static void createRepairOrder() {
        if (!currentUser.hasDorm()) {
            System.out.println("❌ 请先绑定宿舍");
            return;
        }

        System.out.println("\n【创建报修单】");
        System.out.println("设备类型：");
        System.out.println("  1. 水龙头");
        System.out.println("  2. 灯泡");
        System.out.println("  3. 马桶");
        System.out.println("  4. 空调");
        System.out.println("  5. 其他");
        System.out.print("请选择：");
        int typeIdx = readInt(1, 5);
        String[] types = {"", "水龙头", "灯泡", "马桶", "空调", "其他"};
        String fixType = types[typeIdx];

        System.out.print("问题描述：");
        String description = scanner.nextLine();

        System.out.println("优先级：");
        System.out.println("  1. 高");
        System.out.println("  2. 中");
        System.out.println("  3. 低");
        System.out.print("请选择：");
        int priIdx = readInt(1, 3);
        //这里要注意是枚举类型
        RepairOrder.Priority[] priorities = {null, RepairOrder.Priority.HIGH, RepairOrder.Priority.MEDIUM, RepairOrder.Priority.LOW};
        RepairOrder.Priority priority = priorities[priIdx];

        RepairOrder order = new RepairOrder();
        order.setStudentId(currentUser.getId());
        order.setFixType(fixType);
        order.setDescription(description);
        order.setPriority(priority);

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            mapper.insert(order);
            System.out.println("✅ 报修单创建成功！");
            System.out.println("单号：" + order.getOrderNo());
        } catch (Exception e) {
            System.out.println("❌ 创建失败：" + e.getMessage());
        }
    }

    private static void viewMyOrders() {
        System.out.println("\n【我的报修记录】");
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            List<RepairOrder> orders = mapper.selectByStudentId(currentUser.getId());
            if (orders.isEmpty()) {
                System.out.println("暂无报修记录");
                return;
            }
            System.out.println("共 " + orders.size() + " 条\n");
            for (int i = 0; i < orders.size(); i++) {
                RepairOrder o = orders.get(i);
                System.out.printf("%d. 单号：%s\n", i+1, o.getOrderNo());
                System.out.printf("   设备：%s\n", o.getFixType());
                System.out.printf("   描述：%s\n", o.getDescription());
                System.out.printf("   状态：%s\n", o.getStatusText());
                System.out.printf("   优先级：%s\n", o.getPriorityText());
                System.out.printf("   创建时间：%s\n", o.getCreateTime());
                System.out.printf("   最后修改：%s\n", o.getUpdateTime());
                System.out.println("   ---");
            }
        } catch (Exception e) {
            System.out.println("❌ 查询失败：" + e.getMessage());
        }
    }

    private static void cancelOrder() {
        System.out.println("\n【取消报修单】");
        System.out.print("请输入报修单号：");
        String orderNo = scanner.nextLine();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectByOrderNo(orderNo);
            if (order == null) {
                System.out.println("❌ 报修单不存在");
                return;
            }
            if (order.getStudentId() != currentUser.getId()) {
                System.out.println("❌ 只能取消自己的报修单");
                return;
            }
            if (!order.canCancel()) {
                System.out.println("❌ 只能取消【待处理】状态的报修单");
                return;
            }
            System.out.print("确认取消？(y/n)：");
            if (!"y".equalsIgnoreCase(scanner.nextLine())) return;

            order.setStatus(RepairOrder.Status.CANCELLED);
            mapper.update(order);
            System.out.println("✅ 已取消");
        } catch (Exception e) {
            System.out.println("❌ 取消失败：" + e.getMessage());
        }
    }

    private static void changePassword() {
        System.out.println("\n【修改密码】");
        System.out.print("原密码：");
        String oldPwd = scanner.nextLine();
        System.out.print("新密码（至少6位）：");
        String newPwd = scanner.nextLine();
        System.out.print("确认新密码：");
        String confirm = scanner.nextLine();

        if (newPwd.length() < 6) {
            System.out.println("❌ 密码长度至少6位");
            return;
        }
        if (!newPwd.equals(confirm)) {
            System.out.println("❌ 两次密码不一致");
            return;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            User user = mapper.selectById(currentUser.getId());
            if (!EncryptUtil.check(oldPwd, user.getPassword())) {
                System.out.println("❌ 原密码错误");
                return;
            }
            user.setPassword(EncryptUtil.encrypt(newPwd));
            mapper.update(user);
            System.out.println("✅ 修改成功，请重新登录");
            currentUser = null;
        } catch (Exception e) {
            System.out.println("❌ 修改失败：" + e.getMessage());
        }
    }

    // ==================== 管理员菜单 ====================

    private static void showAdminMenu() {
        System.out.println("\n【管理员菜单】");
        System.out.println("当前管理员：" + currentUser.getAccount());

        System.out.println("\n1. 查看所有报修单");
        System.out.println("2. 按状态查看报修单");
        System.out.println("3. 查看报修单详情");
        System.out.println("4. 更新报修单状态");
        System.out.println("5. 删除报修单");
        System.out.println("6. 修改密码");
        System.out.println("7. 退出登录");
        System.out.print("请选择：");

        String choice = scanner.nextLine();
        switch (choice) {
            case "1": viewAllOrders(); break;
            case "2": viewOrdersByStatus(); break;
            case "3": viewOrderDetail(); break;
            case "4": updateOrderStatus(); break;
            case "5": deleteOrder(); break;
            case "6": changePassword(); break;
            case "7": currentUser = null; System.out.println("已退出登录"); break;
            default: System.out.println("输入错误");
        }
    }

    private static void viewAllOrders() {
        System.out.println("\n【所有报修单】");
        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            List<RepairOrder> orders = mapper.selectAllWithStudent();
            if (orders.isEmpty()) {
                System.out.println("暂无报修单");
                return;
            }
            System.out.println("共 " + orders.size() + " 条\n");
            for (int i = 0; i < orders.size(); i++) {
                RepairOrder o = orders.get(i);
                System.out.printf("%d. 单号：%s\n", i+1, o.getOrderNo());
                System.out.printf("   学生：%s\n", o.getStudent().getAccount());
                System.out.printf("   宿舍：%s%s\n", o.getStudent().getBuilding(), o.getStudent().getRoomNumber());
                System.out.printf("   设备：%s\n", o.getFixType());
                System.out.printf("   状态：%s\n", o.getStatusText());
                System.out.printf("   优先级：%s\n", o.getPriorityText());
                System.out.println("   ---");
            }
        } catch (Exception e) {
            System.out.println("❌ 查询失败：" + e.getMessage());
        }
    }

    private static void viewOrdersByStatus() {
        System.out.println("\n【按状态查询】");
        System.out.println("1. 待处理");
        System.out.println("2. 处理中");
        System.out.println("3. 已完成");
        System.out.println("4. 已取消");
        System.out.print("请选择：");
        int choice = readInt(1, 4);
        String status = "";
        String statusText = "";
        switch (choice) {
            case 1: status = "pending"; statusText = "待处理"; break;
            case 2: status = "processing"; statusText = "处理中"; break;
            case 3: status = "completed"; statusText = "已完成"; break;
            case 4: status = "cancelled"; statusText = "已取消"; break;
        }

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            List<RepairOrder> orders = mapper.selectByStatus(status);
            if (orders.isEmpty()) {
                System.out.println("暂无" + statusText + "的报修单");
                return;
            }
            System.out.println("\n【" + statusText + "】共 " + orders.size() + " 条\n");
            for (int i = 0; i < orders.size(); i++) {
                RepairOrder o = orders.get(i);
                System.out.printf("%d. 单号：%s\n", i+1, o.getOrderNo());
                System.out.printf("   学生：%s\n", o.getStudent().getAccount());
                System.out.printf("   宿舍：%s%s\n", o.getStudent().getBuilding(), o.getStudent().getRoomNumber());
                System.out.printf("   设备：%s\n", o.getFixType());
                System.out.printf("   描述：%s\n", o.getDescription());
                System.out.printf("   状态：%s\n", o.getStatusText());
                System.out.printf("   优先级：%s\n", o.getPriorityText());
                System.out.println("   ---");
            }
        } catch (Exception e) {
            System.out.println("❌ 查询失败：" + e.getMessage());
        }
    }

    private static void viewOrderDetail() {
        System.out.println("\n【查看报修单详情】");
        System.out.print("请输入报修单号：");
        String orderNo = scanner.nextLine();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectByOrderNo(orderNo);
            if (order == null) {
                System.out.println("❌ 报修单不存在");
                return;
            }
            System.out.println("\n单号：" + order.getOrderNo());
            System.out.println("学生账号：" + order.getStudent().getAccount());
            System.out.println("宿舍：" + order.getStudent().getBuilding() + order.getStudent().getRoomNumber());
            System.out.println("设备类型：" + order.getFixType());
            System.out.println("问题描述：" + order.getDescription());
            System.out.println("状态：" + order.getStatusText());
            System.out.println("优先级：" + order.getPriorityText());
            System.out.println("创建时间：" + order.getCreateTime());
            System.out.println("最后修改：" + order.getUpdateTime());
        } catch (Exception e) {
            System.out.println("❌ 查询失败：" + e.getMessage());
        }
    }

    private static void updateOrderStatus() {
        System.out.println("\n【更新报修单状态】");
        System.out.print("请输入报修单号：");
        String orderNo = scanner.nextLine();

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectByOrderNo(orderNo);
            if (order == null) {
                System.out.println("❌ 报修单不存在");
                return;
            }
            System.out.println("\n当前状态：" + order.getStatusText());
            System.out.println("选择新状态：");
            System.out.println("  1. 处理中");
            System.out.println("  2. 已完成");
            System.out.println("  3. 已取消");
            System.out.print("请选择：");
            int choice = readInt(1, 3);
            RepairOrder.Status newStatus = null;
            switch (choice) {
                case 1: newStatus = RepairOrder.Status.PROCESSING; break;
                case 2: newStatus = RepairOrder.Status.COMPELITED; break;
                case 3: newStatus = RepairOrder.Status.CANCELLED; break;
            }
            order.setStatus(newStatus);
            mapper.update(order);
            System.out.println("✅ 状态已更新为：" + order.getStatusText());
        } catch (Exception e) {
            System.out.println("❌ 更新失败：" + e.getMessage());
        }
    }

    private static void deleteOrder() {
        System.out.println("\n【删除报修单】");
        System.out.print("请输入报修单号：");
        String orderNo = scanner.nextLine();

        System.out.print("确认删除？(y/n)：");
        if (!"y".equalsIgnoreCase(scanner.nextLine())) return;

        try (SqlSession session = MyBatisUtil.getSqlSession()) {
            RepairOrderMapper mapper = session.getMapper(RepairOrderMapper.class);
            RepairOrder order = mapper.selectByOrderNo(orderNo);
            if (order == null) {
                System.out.println("❌ 报修单不存在");
                return;
            }
            mapper.deleteById(order.getId());
            System.out.println("✅ 已删除");
        } catch (Exception e) {
            System.out.println("❌ 删除失败：" + e.getMessage());
        }
    }

    // 辅助方法：读取整数
    private static int readInt(int min, int max) {
        while (true) {
            try {
                int val = Integer.parseInt(scanner.nextLine());
                if (val >= min && val <= max) return val;
                System.out.print("请输入 " + min + "~" + max + " 的数字：");
            } catch (NumberFormatException e) {
                System.out.print("请输入数字：");
            }
        }
    }
}