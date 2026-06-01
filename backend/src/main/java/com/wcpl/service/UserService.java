package com.wcpl.service;

import com.wcpl.dto.request.UpdateProfileRequest;
import com.wcpl.dto.response.UserProfileResponse;
import com.wcpl.entity.User;
import com.wcpl.exception.AppException;
import com.wcpl.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Value("${app.upload.avatar-dir}")
    private String avatarDir;

    private static final List<String> DEFAULT_AVATARS = List.of(
            // Cầu thủ nổi tiếng
            "messi.png", "ronaldo.png", "mbappe.png", "neymar.png",
            "haaland.png", "modric.png", "salah.png", "son.png",
            "kane.png", "vinicius.png", "benzema.png", "lewandowski.png",
            // Avatar mặc định
            "default_1.png", "default_2.png", "default_3.png", "default_4.png",
            "default_5.png", "default_6.png", "default_7.png", "default_8.png"
    );

    public UserProfileResponse getProfile(Long userId) {
        User user = findUser(userId);
        return toResponse(user);
    }

    public UserProfileResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findUser(userId);
        user.setDisplayName(req.displayName());
        userRepository.save(user);
        return toResponse(user);
    }

    public UserProfileResponse uploadAvatar(Long userId, MultipartFile file) {
        validateAvatarFile(file);
        User user = findUser(userId);

        String ext = getExtension(file.getOriginalFilename());
        String filename = "user_" + userId + "_" + UUID.randomUUID() + "." + ext;

        saveFile(file, filename);
        deleteOldAvatar(user);

        user.setAvatarPath(filename);
        userRepository.save(user);
        return toResponse(user);
    }

    public UserProfileResponse setDefaultAvatar(Long userId, String avatarName) {
        if (!DEFAULT_AVATARS.contains(avatarName)) {
            throw AppException.badRequest("INVALID_AVATAR", "Avatar mặc định không hợp lệ");
        }
        User user = findUser(userId);
        deleteOldAvatar(user);
        user.setAvatarPath("defaults/" + avatarName);
        userRepository.save(user);
        return toResponse(user);
    }

    public List<String> getDefaultAvatars() {
        return DEFAULT_AVATARS.stream()
                .map(a -> "/avatars/defaults/" + a)
                .toList();
    }

    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw AppException.badRequest("EMPTY_FILE", "File không được để trống");
        }
        String ct = file.getContentType();
        if (ct == null || !ct.startsWith("image/")) {
            throw AppException.badRequest("INVALID_FILE_TYPE", "Chỉ chấp nhận file ảnh");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw AppException.badRequest("FILE_TOO_LARGE", "File tối đa 5MB");
        }
    }

    private void saveFile(MultipartFile file, String filename) {
        try {
            Path dir = Path.of(avatarDir);
            Files.createDirectories(dir);
            file.transferTo(Objects.requireNonNull(dir.resolve(filename)));
        } catch (IOException e) {
            throw AppException.badRequest("UPLOAD_FAILED", "Không thể lưu file");
        }
    }

    private void deleteOldAvatar(User user) {
        String existing = user.getAvatarPath();
        if (existing != null && !existing.startsWith("defaults/")) {
            try {
                Files.deleteIfExists(Path.of(avatarDir, existing));
            } catch (IOException ignored) {}
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    private User findUser(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> AppException.notFound("Tài khoản không tồn tại"));
    }

    public UserProfileResponse toResponse(User user) {
        String avatarUrl = user.getAvatarPath() != null ? "/avatars/" + user.getAvatarPath() : null;
        return new UserProfileResponse(
                user.getId(), user.getUsername(), user.getDisplayName(),
                avatarUrl, user.getCredits(), user.getRole(), user.getCreatedAt()
        );
    }
}
