-- Database: residence-management

-- Table: departments
CREATE TABLE departments (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             code VARCHAR(50) UNIQUE NOT NULL,
                             name VARCHAR(100) NOT NULL,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: employees
CREATE TABLE employees (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           department_id INT,
                           full_name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20),
                           email VARCHAR(100) UNIQUE NOT NULL,
                           password_hash VARCHAR(255) NOT NULL,
                           role VARCHAR(50) NOT NULL COMMENT 'ADMIN, MANAGER, STAFF',
                           is_active BOOLEAN DEFAULT TRUE,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           FOREIGN KEY (department_id) REFERENCES departments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

--Table: projects
CREATE TABLE projects ( -- Bảng hàng (Ví dụ: Lumi Hà Nội)
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          code VARCHAR(50) UNIQUE NOT NULL,
                          name VARCHAR(150) NOT NULL,
                          status VARCHAR(50) DEFAULT 'ACTIVE',
                          display_order INT DEFAULT 0,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: zones
CREATE TABLE zones ( -- Phân khu
                       id INT AUTO_INCREMENT PRIMARY KEY,
                       project_id INT NOT NULL,
                       code VARCHAR(50) NOT NULL,
                       name VARCHAR(150) NOT NULL,
                       display_order INT DEFAULT 0,
                       FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: apartment_types
CREATE TABLE apartment_types ( -- Loại căn hộ (Ví dụ: Studio, 1PN+1)
                                 id INT AUTO_INCREMENT PRIMARY KEY,
                                 zone_id INT NOT NULL,
                                 code VARCHAR(50),
                                 name VARCHAR(100) NOT NULL,
                                 display_order INT DEFAULT 0,
                                 FOREIGN KEY (zone_id) REFERENCES zones(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: apartments
CREATE TABLE apartments (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- Foreign Keys (Bao gồm cả khử chuẩn để query nhanh)
                            project_id INT NOT NULL,
                            zone_id INT NOT NULL,
                            apartment_type_id INT NOT NULL,

    -- Thông tin cơ bản
                            code VARCHAR(50) UNIQUE NOT NULL COMMENT 'Mã căn (S1.01-12A05)',
                            hashed_code VARCHAR(100) COMMENT 'Mã căn hóa để share public',
                            area DECIMAL(8, 2) NOT NULL COMMENT 'Diện tích tim tường/thông thủy',
                            price DECIMAL(15, 2) NOT NULL COMMENT 'Giá bán (VND)',
                            tax_fee DECIMAL(15, 2) DEFAULT 0 COMMENT 'Thuế phí (VND)',
                            furniture_status VARCHAR(100) COMMENT 'Tình trạng nội thất (Thô, Cơ bản, Full)',
                            legal_status VARCHAR(100) COMMENT 'TT sổ đỏ + vay',
                            balcony_direction VARCHAR(50) COMMENT 'Hướng ban công (Đông, Tây, Nam, Bắc...)',
                            note TEXT COMMENT 'Ghi chú',

    -- Thông tin liên hệ (Cần được bảo mật ở tầng Application)
                            owner_phone VARCHAR(50) COMMENT 'SĐT chủ nhà',
                            owner_contact VARCHAR(255) COMMENT 'Liên hệ khác',
                            source VARCHAR(150) COMMENT 'Báo nguồn (Tên sale/đại lý)',

    -- Trạng thái
                            status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE, RESERVED, SOLD, LOCKED',

    -- Audit & Soft Delete
                            created_by INT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            deleted_at TIMESTAMP NULL DEFAULT NULL COMMENT 'Dùng cho Soft Delete',

    -- Indexes cho hiệu năng tìm kiếm
                            INDEX idx_project_zone_type (project_id, zone_id, apartment_type_id),
                            INDEX idx_status (status),
                            INDEX idx_price (price),

                            FOREIGN KEY (project_id) REFERENCES projects(id),
                            FOREIGN KEY (zone_id) REFERENCES zones(id),
                            FOREIGN KEY (apartment_type_id) REFERENCES apartment_types(id),
                            FOREIGN KEY (created_by) REFERENCES employees(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Table: apartment_media
CREATE TABLE apartment_media (
                                 id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                 apartment_id BIGINT NOT NULL,
                                 media_type VARCHAR(20) NOT NULL COMMENT 'IMAGE, VIDEO, FILE',
                                 url VARCHAR(500) NOT NULL COMMENT 'Đường dẫn file gốc (S3/CDN)',
                                 thumbnail_url VARCHAR(500) COMMENT 'Đường dẫn ảnh thu nhỏ (cho list view)',
                                 is_primary BOOLEAN DEFAULT FALSE COMMENT 'Là ảnh đại diện/thumbnail chính',
                                 display_order INT DEFAULT 0,
                                 created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                 FOREIGN KEY (apartment_id) REFERENCES apartments(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;