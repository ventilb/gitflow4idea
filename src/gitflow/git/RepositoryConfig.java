package gitflow.git;

import git4idea.repo.GitRepository;

/**
 * Implements a simple java bean to hold the gitflow configuration for a specific git repository.
 *
 * @author <a href="mailto:manuel_schulze@i-entwicklung.de">Manuel Schulze</a>
 * @since 09.06.14 - 16:41
 */
public class RepositoryConfig {
    private final GitRepository gitRepository;

    private String productionBranch;
    private String developmentBranch;
    private String featurePrefix;
    private String releasePrefix;
    private String hotfixPrefix;
    private String supportPrefix;
    private String versionPrefix;

    public RepositoryConfig(final GitRepository gitRepository) {
        this.gitRepository = gitRepository;
    }

    public GitRepository getGitRepository() {
        return gitRepository;
    }

    public String getProductionBranch() {
        return productionBranch;
    }

    public void setProductionBranch(String productionBranch) {
        this.productionBranch = productionBranch;
    }

    public String getDevelopmentBranch() {
        return developmentBranch;
    }

    public void setDevelopmentBranch(String developmentBranch) {
        this.developmentBranch = developmentBranch;
    }

    public String getFeaturePrefix() {
        return featurePrefix;
    }

    public void setFeaturePrefix(String featurePrefix) {
        this.featurePrefix = featurePrefix;
    }

    public String getReleasePrefix() {
        return releasePrefix;
    }

    public void setReleasePrefix(String releasePrefix) {
        this.releasePrefix = releasePrefix;
    }

    public String getHotfixPrefix() {
        return hotfixPrefix;
    }

    public void setHotfixPrefix(String hotfixPrefix) {
        this.hotfixPrefix = hotfixPrefix;
    }

    public String getSupportPrefix() {
        return supportPrefix;
    }

    public void setSupportPrefix(String supportPrefix) {
        this.supportPrefix = supportPrefix;
    }

    public String getVersionPrefix() {
        return versionPrefix;
    }

    public void setVersionPrefix(String versionPrefix) {
        this.versionPrefix = versionPrefix;
    }

}
