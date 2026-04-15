package com.base.app.project.handler;

import com.base.app.project.dto.ProjectManagementSidebarDto;
import com.base.domain.project.domain.Project;
import com.base.domain.project.repository.ProjectManagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CreateProjectHandler {

    private final ProjectManagementRepository projectManagementRepository;

    public ProjectManagementSidebarDto handle(final CreateProjectCommand command) {
        final String name = command.name().strip();
        final String codeResolved = resolveUniqueCode(name, command.code() == null ? null : command.code().strip());
        Project project = Project.createNew(codeResolved, name);
        Project saved = projectManagementRepository.save(project);
        log.info("Project created id={} code={}", saved.getId(), saved.getCode());
        return ProjectManagementSidebarDto.fromDomain(saved);
    }

    private String resolveUniqueCode(final String name, final String explicitCodeOrNull) {
        if (explicitCodeOrNull != null && !explicitCodeOrNull.isEmpty()) {
            final String c = normalizeToCode(explicitCodeOrNull);
            if (c.isEmpty()) {
                throw new IllegalArgumentException("Project code must contain letters or digits.");
            }
            if (projectManagementRepository.existsByCode(c)) {
                throw new IllegalArgumentException("Project with this code already exists: " + c);
            }
            return c;
        }
        String base = normalizeToCode(name);
        if (base.isEmpty()) {
            base = "PROJECT";
        }
        base = base.length() > 50 ? base.substring(0, 50) : base;
        String candidate = base;
        int n = 2;
        while (projectManagementRepository.existsByCode(candidate)) {
            String suffix = "_" + n;
            int maxBaseLen = Math.max(1, 50 - suffix.length());
            candidate = base.substring(0, Math.min(base.length(), maxBaseLen)) + suffix;
            n++;
        }
        return candidate;
    }

    private static String normalizeToCode(final String raw) {
        String normalized = Normalizer.normalize(raw, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        String upper = normalized.toUpperCase(Locale.ROOT);
        String slug = upper.replaceAll("[^A-Z0-9]+", "_");
        slug = slug.replaceAll("^_+", "").replaceAll("_+$", "").replaceAll("_+", "_");
        return slug.length() > 50 ? slug.substring(0, 50) : slug;
    }
}
